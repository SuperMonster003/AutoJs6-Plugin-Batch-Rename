"""Generates the pinyin dictionary of the APK (roadmap P6.1, D35) from the pinned data in third_party/.

    py .python/generate_pinyin_dictionary.py           writes app/src/main/resources/pinyin/*.txt
    py .python/generate_pinyin_dictionary.py --check   fails when the committed files differ

Outputs (UTF-8, LF, one entry per line after a "#" header line):
- chars.txt: "<character><TAB><default reading>", the customary reading of every character pinyin-data knows,
  sorted by code point.
- phrases.txt: "<phrase><TAB><syllables>", only the phrases whose readings differ from the default readings of their
  characters (the others add nothing), plus every correction of .python/pinyin_overwrite.txt.
- t2s.txt: "<traditional><TAB><simplified>", single characters from OpenCC, used to look up traditional phrases.
- NOTICE.txt: where the data comes from and the three upstream license texts, shipped with the data in the APK.
"""
from __future__ import annotations

import argparse
import hashlib
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
THIRD_PARTY = ROOT / "third_party"
OVERWRITE = ROOT / ".python" / "pinyin_overwrite.txt"
OUTPUT = ROOT / "app" / "src" / "main" / "resources" / "pinyin"
FORMAT = 1
TAB = chr(9)
# The upstream files of third_party/README.md; a changed byte fails both modes.
PINNED = {
    "pinyin-data/pinyin.txt": "621f8ca9eff8519f47e2b17b564fd318161e13bca07eea8c8e04993cd5d3b52e",
    "pinyin-data/LICENSE": "9c048697be2502a16e8bcb282d5d465a07295b2def0ffb05a269c5d39dbe1586",
    "phrase-pinyin-data/pinyin.txt": "dcc769607c220b312fea3e71cb63421298b4b891b1f7356a95ab58f2c96fff81",
    "phrase-pinyin-data/LICENSE": "89ac55df747e4776088c3e77531ef61b973a1a59dd8e6a4548a58996da9a4f70",
    "opencc/TSCharacters.txt": "110d9bc9493916bcff597cc68dd9465fde58fa3e99e1e3f11db7fea83a192f58",
    "opencc/LICENSE": "d4d265b6ef2cfd0fb645acee9d9092103d5fadbdae887eb87c58a5d9b0780e19",
}
# Lowercase Latin letters with tone marks and the combining marks some syllables use (m̄, ê̌).
SYLLABLE = re.compile(r"[a-zāáǎàēéěèêīíǐìōóǒòūúǔùüǖǘǚǜḿńňǹ\u0300-\u036f]+")


def entries(path: Path):
    for number, raw in enumerate(path.read_text(encoding="utf-8").splitlines(), 1):
        line = raw.split("#", 1)[0].strip()
        if line:
            yield number, line


def read_characters() -> dict[int, str]:
    result = {}
    for number, line in entries(THIRD_PARTY / "pinyin-data" / "pinyin.txt"):
        code, readings = (part.strip() for part in line.split(":", 1))
        default = readings.split(",")[0].strip()
        if not code.startswith("U+") or not SYLLABLE.fullmatch(default):
            raise SystemExit(f"pinyin-data line {number}: unexpected entry {line!r}")
        result[int(code[2:], 16)] = default
    return result


def read_phrases(path: Path, label: str) -> dict[str, list[str]]:
    result = {}
    for number, line in entries(path):
        word, readings = (part.strip() for part in line.split(":", 1))
        syllables = readings.split()
        if len(syllables) != len(word) or not all(SYLLABLE.fullmatch(s) for s in syllables):
            if label == "overwrite":
                raise SystemExit(f"{label} line {number}: syllables do not match {word!r}")
            continue
        result.setdefault(word, syllables)
    return result


def read_traditional() -> dict[str, str]:
    result = {}
    for _, line in entries(THIRD_PARTY / "opencc" / "TSCharacters.txt"):
        parts = line.split(TAB)
        if len(parts) != 2 or len(parts[0]) != 1:
            continue
        simplified = parts[1].split(" ")[0]
        if len(simplified) == 1 and simplified != parts[0]:
            result[parts[0]] = simplified
    return result


def verify_pinned() -> None:
    for relative, digest in PINNED.items():
        actual = hashlib.sha256((THIRD_PARTY / relative).read_bytes()).hexdigest()
        if actual != digest:
            raise SystemExit(f"third_party/{relative} differs from the pinned upstream file (sha256 {actual})")


def notice() -> str:
    parts = [
        "The pinyin dictionary of Batch Rename (chars.txt, phrases.txt, t2s.txt) is derived from:\n",
        "- pinyin-data, https://github.com/mozillazg/pinyin-data (MIT)\n",
        "- phrase-pinyin-data, https://github.com/mozillazg/phrase-pinyin-data (MIT)\n",
        "- OpenCC TSCharacters.txt, https://github.com/BYVoid/OpenCC (Apache-2.0)\n",
    ]
    for name, relative in [("pinyin-data", "pinyin-data/LICENSE"), ("phrase-pinyin-data", "phrase-pinyin-data/LICENSE"),
                           ("OpenCC", "opencc/LICENSE")]:
        text = (THIRD_PARTY / relative).read_bytes().decode("utf-8").replace("\r\n", "\n").rstrip("\n")
        parts.append(f"\n===== {name} license =====\n\n{text}\n")
    return "".join(parts)


def build() -> dict[str, str]:
    verify_pinned()
    characters = read_characters()
    overwrite = read_phrases(OVERWRITE, "overwrite")
    for word, syllables in overwrite.items():
        if len(word) == 1:
            characters[ord(word)] = syllables[0]
    phrases = read_phrases(THIRD_PARTY / "phrase-pinyin-data" / "pinyin.txt", "phrase-pinyin-data")
    phrases.update({word: syllables for word, syllables in overwrite.items() if len(word) > 1})

    def default(character: str) -> str | None:
        return characters.get(ord(character))

    needed = {
        word: syllables for word, syllables in phrases.items()
        if word in overwrite or [default(c) for c in word] != syllables
    }
    traditional = read_traditional()
    header = f"# Batch Rename pinyin dictionary format {FORMAT}, generated by .python/generate_pinyin_dictionary.py from third_party/ (see third_party/README.md)\n"
    return {
        "NOTICE.txt": notice(),
        "chars.txt": header + "".join(f"{chr(code)}{TAB}{reading}\n" for code, reading in sorted(characters.items())),
        "phrases.txt": header + "".join(f"{word}{TAB}{' '.join(needed[word])}\n" for word in sorted(needed)),
        "t2s.txt": header + "".join(f"{t}{TAB}{traditional[t]}\n" for t in sorted(traditional)),
    }


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--check", action="store_true")
    args = parser.parse_args()
    outputs = build()
    stale = []
    for name, text in outputs.items():
        path = OUTPUT / name
        data = text.encode("utf-8")
        lines = text.count("\n") - 1
        if args.check:
            if not path.is_file() or path.read_bytes() != data:
                stale.append(name)
        else:
            path.parent.mkdir(parents=True, exist_ok=True)
            path.write_bytes(data)
        print(f"{name}: {lines} entries, {len(data)} bytes")
    if stale:
        print("PINYIN_DICTIONARY_STALE " + ", ".join(stale) + " (run py .python/generate_pinyin_dictionary.py)")
        return 1
    print("PINYIN_DICTIONARY_OK mode=" + ("check" if args.check else "write"))
    return 0


if __name__ == "__main__":
    sys.exit(main())
