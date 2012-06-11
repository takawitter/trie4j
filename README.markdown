# Trie4J - various trie implementation for Java.

[![Build Status](https://buildhive.cloudbees.com/job/takawitter/job/trie4j/badge/icon)](https://buildhive.cloudbees.com/job/takawitter/job/trie4j/)
 **latest [trie4j-SNAPSHOT.jar](https://buildhive.cloudbees.com/job/takawitter/job/trie4j/lastSuccessfulBuild/artifact/trie4j/dist/trie4j-SNAPSHOT.jar)**

---
Sample codes:

	import org.trie4j.doublearray.DoubleArray;
	import org.trie4j.louds.LOUDSTrie;
	import org.trie4j.patricia.simple.PatriciaTrie;

	public class Sample {
		public static void main(String[] args) throws Exception{
			PatriciaTrie pat = new PatriciaTrie();
			pat.insert("Hello");
			pat.insert("World");
			pat.insert("Wonder");
			pat.insert("Wonderful!");
			pat.contains("Hello"); // -> true
			pat.predictiveSearch("Wo"); // -> {"Wonder", "Wonderful!", "World"} as Iterable<String>
			
			DoubleArray da = new DoubleArray(pat); // construct DoubleArray from existing Trie
			da.contains("World"); // -> true
			
			LOUDSTrie lt = new LOUDSTrie(pat); // construct LOUDS succinct Trie
			lt.contains("Wonderful!"); // -> true
			lt.commonPrefixSearch("Wonderful!"); // -> {"Wonder", "Wonderful!"} as Iterable<String>
		}
	}

---
Currently Trie4J has following implementation:
* patricia trie
 * Simple Patricia Trie(no size optimization)  - [org.trie4j.patricia.simple.PatriciaTrie](https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/patricia/simple/PatriciaTrie.java)
 * Multilayer Patricia Trie(optimizes size using Multilayer Trie) - [org.trie4j.patricia.multilayer.MultilayerPatriciaTrie](https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/patricia/multilayer/MultilayerPatriciaTrie.java)
* double array
 * Simple Double Array (no size optimization) - [org.trie4j.doublearray.DoubleArray](https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/doublearray/DoubleArray.java)
 * Double Array with Tail (store char sequence in one string(tails)) - [org.trie4j.doublearray.TailDoubleArray](https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/doublearray/TailDoubleArray.java)
 * Double Array with compacted Tail (shrink tail by inverse suffix patricia trie) - [org.trie4j.doublearray.TailCompactionDoubleArray](https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/doublearray/TailCompactionDoubleArray.java)
 * Double Array with compacted Tail and some optimization - [org.trie4j.doublearray.OptimizedTailCompactionDoubleArray](https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/doublearray/OptimizedTailCompactionDoubleArray.java)
* LOUDS(Level-Order Unary Degree Sequence) Trie
 * Simple LOUDS Trie - [org.trie4j.louds.LOUDSTrie](https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/louds/LOUDSTrie.java)

**This project is the state of experimental, so the API might be changed without notice.**

---
2012年2月、1冊の本が発売されました。
[![日本語入力を支える技術](http://ws.assoc-amazon.jp/widgets/q?_encoding=UTF8&Format=_SL110_&ASIN=4774149934&MarketPlace=JP&ID=AsinImage&WS=1&tag=takaoblogspot-22&ServiceVersion=20070822)](http://www.amazon.co.jp/gp/product/4774149934/ref=as_li_ss_il?ie=UTF8&tag=takaoblogspot-22&linkCode=as2&camp=247&creative=7399&creativeASIN=4774149934)

多くのエンジニアがこの本に触発され、各種アルゴリズムの理解を深めたり、いちから勉強を始めたり、
また中にはこれを機に様々なライブラリを実装し公開する人も出てきました。
Trie4Jもそういったライブラリの一つで、各種トライ構造にターゲットを絞り、本書やその分野のブログなどを参考に実装されています。
現在以下のクラスがあります。

* パトリシアトライ
 * シンプルなパトリシアトライ(サイズ最適化無し) - [org.trie4j.patricia.simple.PatriciaTrie](https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/patricia/simple/PatriciaTrie.java)
 * 多層パトリシアトライ(接尾辞を格納するトライを内包しサイズを最適化。参考: [多層トライの実験結果 - やた＠はてな日記](http://d.hatena.ne.jp/s-yata/20101223/1293143633) ) - [org.trie4j.patricia.multilayer.MultilayerPatriciaTrie](https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/patricia/multilayer/MultilayerPatriciaTrie.java)
* ダブルアレイ(又はダブル配列)
 * シンプルなダブルアレイ(サイズ最適化無し) - [org.trie4j.doublearray.DoubleArray](https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/doublearray/DoubleArray.java)
 * TAIL付きダブルアレイ(子が一つだけのノードが連続する場合に文字列としてTAIL配列に格納) - [org.trie4j.doublearray.TailDoubleArray](https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/doublearray/TailDoubleArray.java)
 * TAIL圧縮ダブルアレイ(多層トライの要領でTAIL配列を圧縮) - [org.trie4j.doublearray.TailCompactionDoubleArray](https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/doublearray/TailCompactionDoubleArray.java)
 * 最適化TAIL圧縮ダブルアレイ(未使用領域の開放やcheck配列をshortにした) - [org.trie4j.doublearray.OptimizedTailCompactionDoubleArray](https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/doublearray/OptimizedTailCompactionDoubleArray.java)
* LOUDS(Level-order unary degree structure) Trie
 * シンプルなLOUDSトライ - [org.trie4j.louds.LOUDSTrie](https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/louds/LOUDSTrie.java)

**このプロジェクトはまだ実験的なものなので、将来APIが変わる可能性があります。**


