# Trie4J - various trie implementation for Java.

[![Build Status](https://buildhive.cloudbees.com/job/takawitter/job/trie4j/badge/icon)](https://buildhive.cloudbees.com/job/takawitter/job/trie4j/)
 **latest [trie4j-SNAPSHOT.jar](https://buildhive.cloudbees.com/job/takawitter/job/trie4j/lastSuccessfulBuild/artifact/trie4j/dist/trie4j-SNAPSHOT.jar)
---
Currently Trie4J has following implementation:
* patricia trie
 * Simple Patricia Trie(no size optimization)  - [org.trie4j.patricia.simple.PatriciaTrie](https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/patricia/simple/PatriciaTrie.java)
 * Multilayer Patricia Trie(optimizes size using Multilayer Trie) - [org.trie4j.patricia.multilayer.MultilayerPatriciaTrie](https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/patricia/multilayer/MultilayerPatriciaTrie.java)
* double array
 * Simple Double Array (no size optimization) - [org.trie4j.doublearray.DoubleArray](https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/doublearray/DoubleArray.java)
 * Double Array with Tail (store char sequence in one string(tails)) - [org.trie4j.doublearray.TailDoubleArray](https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/doublearray/TailDoubleArray.java)
 * Double Array with compacted Tail (shrink tail by inverse suffix patricia trie) - [org.trie4j.doublearray.TailCompactionDoubleArray](https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/doublearray/TailCompactionDoubleArray.java)
 * Double Array with compacted Tail and some optimization - [org.trie4j.doublearray.OptimizedTailCompactionDoubleArray](https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/doublearray/OptimizedTailCompcationDoubleArray.java)

**This project is the state of experimental, so the API might be changed without notice.** Please contact me if you need API stability, then I will try to re-design stable API and implement it :)

---
Trie4Jは、Javaで各種トライを実装したライブラリです。現在以下のクラスがあります。
* パトリシアトライ
 * シンプルなパトリシアトライ(サイズ最適化無し) - [org.trie4j.patricia.simple.PatriciaTrie](https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/patricia/simple/PatriciaTrie.java)
 * 多層パトリシアトライ(接尾辞を格納するトライを内包しサイズを最適化。参考: [多層トライの実験結果 - やた＠はてな日記](http://d.hatena.ne.jp/s-yata/20101223/1293143633) ) - [org.trie4j.patricia.multilayer.MultilayerPatriciaTrie](https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/patricia/multilayer/MultilayerPatriciaTrie.java)
* ダブルアレイ(又はダブル配列)
 * シンプルなダブルアレイ(サイズ最適化無し) - [org.trie4j.doublearray.DoubleArray](https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/doublearray/DoubleArray.java)
 * TAIL付きダブルアレイ(子が一つだけのノードが連続する場合に文字列としてTAIL配列に格納) - [org.trie4j.doublearray.TailDoubleArray](https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/doublearray/TailDoubleArray.java)
 * TAIL圧縮ダブルアレイ(多層トライの要領でTAIL配列を圧縮) - [org.trie4j.doublearray.TailCompactionDoubleArray](https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/doublearray/TailCompactionDoubleArray.java)
 * 最適化TAIL圧縮ダブルアレイ(未使用領域の開放やcheck配列をshortにした) - [org.trie4j.doublearray.OptimizedTailCompactionDoubleArray](https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/doublearray/OptimizedTailCompactionDoubleArray.java)

**このプロジェクトはまだ実験的なものなので、将来APIが変わる可能性があります。**
安定したAPIが必要な場合は連絡下さい。API再設計を優先して作業します。
