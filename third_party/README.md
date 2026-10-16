# Third-party data

Upstream files kept byte for byte (`.gitattributes` marks them `-text`). `.python/generate_pinyin_dictionary.py`
derives the pinyin dictionary of the APK (`app/src/main/resources/pinyin/`) from them; it refuses files whose SHA-256
differs from the table, and `--check` (run by the Build integrity workflow) verifies that the generated files match.

| Path | Upstream | Pinned commit | License | SHA-256 |
| --- | --- | --- | --- | --- |
| `pinyin-data/pinyin.txt` | https://github.com/mozillazg/pinyin-data (`pinyin.txt`: readings of single characters, the customary reading first) | `9193766130af24d2ac54230be979b2e98ac66223` | MIT (`pinyin-data/LICENSE`) | `621f8ca9eff8519f47e2b17b564fd318161e13bca07eea8c8e04993cd5d3b52e` |
| `phrase-pinyin-data/pinyin.txt` | https://github.com/mozillazg/phrase-pinyin-data (`pinyin.txt`: phrase readings with the upstream manual corrections; `large_pinyin.txt` is not used because it merges CC-CEDICT data under CC BY-SA) | `cee0ed6e6e4898580cafd2bd5e3723e20b214aa0` | MIT (`phrase-pinyin-data/LICENSE`) | `dcc769607c220b312fea3e71cb63421298b4b891b1f7356a95ab58f2c96fff81` |
| `opencc/TSCharacters.txt` | https://github.com/BYVoid/OpenCC `ver.1.4.2` (`data/dictionary/TSCharacters.txt`: traditional to simplified characters), copied from the OpenCC source in AutoJs6-Plugin-Three-Hanzi-CC | `025f371dc76b598d77384fbdab90c937471844d8` | Apache-2.0 (`opencc/LICENSE`) | `110d9bc9493916bcff597cc68dd9465fde58fa3e99e1e3f11db7fea83a192f58` |

License texts: `pinyin-data/LICENSE` (`9c048697be2502a16e8bcb282d5d465a07295b2def0ffb05a269c5d39dbe1586`),
`phrase-pinyin-data/LICENSE` (`89ac55df747e4776088c3e77531ef61b973a1a59dd8e6a4548a58996da9a4f70`),
`opencc/LICENSE` (`d4d265b6ef2cfd0fb645acee9d9092103d5fadbdae887eb87c58a5d9b0780e19`).

Corrections of this repository live in `.python/pinyin_overwrite.txt` (MPL-2.0) and take precedence over the upstream
phrases.
