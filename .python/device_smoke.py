"""Release smoke test of roadmap gate R3 on one device.

Installs the AutoJs6 debug build and its instrumentation APK from a host checkout, replaces the Batch Rename plugin
with the given APK (uninstalling a differently signed copy first), force-stops the plugin so that the host has to
wake it as after a fresh install, and runs the host tests that cover the three file manager entries and the
script API (plan / apply / undo / history). Prints one line per step with pass / fail and the key numbers only.

    py .python/device_smoke.py --serial <serial> --plugin-apk releases/<apk> [--host D:/idea-projects/AutoJs6]
        [--host-abi arm64-v8a] [--skip-host-install]

Phone dialogs (installation confirmations on some vendors) are left to the person at the phone.
"""
from __future__ import annotations

import argparse
import hashlib
import re
import subprocess
import sys
from pathlib import Path

PLUGIN_PACKAGE = "io.github.supermonster003.autojs6.plugin.batch.rename"
HOST_PACKAGE = "org.autojs.autojs6"
RUNNER = "org.autojs.autojs6.test/androidx.test.runner.AndroidJUnitRunner"
TEST_CLASSES = [
    "org.autojs.autojs.core.plugin.batchrename.BatchRenameExplorerFlowTest",
    "org.autojs.autojs.core.plugin.batchrename.BatchRenameScriptApiTest",
    "org.autojs.autojs.core.plugin.batchrename.BatchRenamePluginRoundTripTest",
]


def adb(serial: str, *args: str, timeout: int = 600) -> subprocess.CompletedProcess[str]:
    return subprocess.run(
        ["adb", "-s", serial, *args],
        capture_output=True, text=True, encoding="utf-8", errors="replace", timeout=timeout,
    )


def shell(serial: str, command: str) -> str:
    return adb(serial, "shell", command).stdout.strip()


def report(step: str, ok: bool, detail: str = "") -> bool:
    print(f"{'ok  ' if ok else 'FAIL'} {step}{': ' + detail if detail else ''}", flush=True)
    return ok


def install(serial: str, apk: Path) -> tuple[bool, str]:
    result = adb(serial, "install", "-r", "-t", str(apk))
    output = (result.stdout + result.stderr).strip().splitlines()
    return "Success" in result.stdout, output[-1] if output else ""


def version_code(serial: str, package: str) -> str:
    match = re.search(r"versionCode=(\d+)", shell(serial, f"dumpsys package {package}"))
    return match.group(1) if match else "-"


def run_tests(serial: str, test_class: str) -> tuple[bool, str]:
    result = adb(serial, "shell", "am", "instrument", "-w", "-r", "-e", "class", test_class, RUNNER, timeout=1200)
    output = result.stdout
    ok = re.search(r"^OK \((\d+) tests?\)", output, re.MULTILINE)
    if ok:
        return True, f"{ok.group(1)} passed"
    failures = re.search(r"Tests run: (\d+),\s+Failures: (\d+)", output)
    first = re.search(r"INSTRUMENTATION_STATUS: stack=(.+)", output)
    detail = f"run {failures.group(1)}, failed {failures.group(2)}" if failures else "no result"
    if first:
        detail += f"; {first.group(1)[:160]}"
    return False, detail


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--serial", required=True)
    parser.add_argument("--plugin-apk", required=True, type=Path)
    parser.add_argument("--host", default=Path("D:/idea-projects/AutoJs6"), type=Path)
    parser.add_argument("--host-abi", default="arm64-v8a")
    parser.add_argument("--skip-host-install", action="store_true")
    args = parser.parse_args()
    serial = args.serial
    passed = True

    model = shell(serial, "getprop ro.product.model")
    sdk = shell(serial, "getprop ro.build.version.sdk")
    digest = hashlib.sha256(args.plugin_apk.read_bytes()).hexdigest()
    print(f"device {serial}: {model}, API {sdk}; plugin APK {args.plugin_apk.name} sha256 {digest[:16]}", flush=True)

    if not args.skip_host_install:
        outputs = args.host / "app" / "build" / "outputs" / "apk"
        for apk in [outputs / "app" / "debug" / f"autojs6-v6.8.0-{args.host_abi}.apk",
                    outputs / "androidTest" / "app" / "debug" / "app-app-debug-androidTest.apk"]:
            ok, line = install(serial, apk)
            passed &= report(f"install {apk.name}", ok, "" if ok else line)
    report("host versionCode", True, version_code(serial, HOST_PACKAGE))

    ok, line = install(serial, args.plugin_apk)
    if not ok and "INSTALL_FAILED_UPDATE_INCOMPATIBLE" in line:
        adb(serial, "uninstall", PLUGIN_PACKAGE)
        ok, line = install(serial, args.plugin_apk)
        line = "reinstalled after removing a differently signed copy" if ok else line
    passed &= report("install plugin", ok, line if not ok or "reinstalled" in line else f"versionCode {version_code(serial, PLUGIN_PACKAGE)}")
    if not ok:
        return 1

    shell(serial, f"am force-stop {PLUGIN_PACKAGE}")
    stopped = re.search(r"stopped=(\w+)", shell(serial, f"dumpsys package {PLUGIN_PACKAGE}"))
    report("plugin force-stopped", True, f"stopped={stopped.group(1) if stopped else '?'}")

    for test_class in TEST_CLASSES:
        ok, detail = run_tests(serial, test_class)
        passed &= report(test_class.rsplit(".", 1)[-1], ok, detail)
    print(f"result {serial}: {'PASS' if passed else 'FAIL'}", flush=True)
    return 0 if passed else 1


if __name__ == "__main__":
    sys.exit(main())
