# Trie4J - various trie implementation for Java.

[![Build Status](https://buildhive.cloudbees.com/job/takawitter/job/trie4j/badge/icon)](https://buildhive.cloudbees.com/job/takawitter/job/trie4j/)
 **latest [trie4j-SNAPSHOT.jar](https://buildhive.cloudbees.com/job/takawitter/job/trie4j/lastSuccessfulBuild/artifact/trie4j/dist/trie4j-SNAPSHOT.jar)**

---
Sample codes:

	import org.trie4j.doublearray.DoubleArray;
	import org.trie4j.louds.LOUDSTrie;
	import org.trie4j.patricia.simple.PatriciaTrie;
	import org.trie4j.tail.ConcatTailBuilder;

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
			
			LOUDSTrie lt = new LOUDSTrie(pat, 1024, new ConcatTailBuilder()); // construct LOUDS succinct Trie with ConcatTailBuilder
			lt.contains("Wonderful!"); // -> true
			lt.commonPrefixSearch("Wonderful!"); // -> {"Wonder", "Wonderful!"} as Iterable<String>
		}
	}

---
Currently Trie4J has following implementations:
* patricia trie
    * Simple Patricia Trie(no size optimization)
        * [org.trie4j.patricia.simple.PatriciaTrie](https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/patricia/simple/PatriciaTrie.java)
    * Multilayer Patricia Trie(optimizes size using Multilayer Trie)
        * [org.trie4j.patricia.multilayer.MultilayerPatriciaTrie](https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/patricia/multilayer/MultilayerPatriciaTrie.java)
    * Patricia Trie with Tail Array(use tail array to store labels)
        * [org.trie4j.patricia.tail.TailPatriciaTrie](https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/patricia/tail/TailPatriciaTrie.java)
* double array
    * Simple Double Array (no size optimization)
        * [org.trie4j.doublearray.DoubleArray](https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/doublearray/DoubleArray.java)
    * Double Array with Tail Array (use tail array to store labels)
        * [org.trie4j.doublearray.TailDoubleArray](https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/doublearray/TailDoubleArray.java)
* LOUDS(Level-Order Unary Degree Sequence) Succinct Trie
    * LOUDS Trie with Tail Array
        * [org.trie4j.louds.LOUDSTrie](https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/louds/LOUDSTrie.java)

These classes are experimental and not contained in trie4j-SNAPSHOT.jar.
* double array
    * Double Array with Tail Array and some optimization (feature completed but can't support large (over several 10 thoudsants) data).
        * [org.trie4j.doublearray.OptimizedTailDoubleArray](https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/doublearray/OptimizedTailDoubleArray.java)

**You can choose Tail Array implementation (Concat (single string) tail array or SuffixTrie (compact string with suffix trie) tail array).**

---
2012年2月、1冊の本が発売されました。

"日本語入力を支える技術" 変わり続けるコンピュータと言葉の世界 (WEB+DB PRESS plus) 徳永 拓之 (著) 

 [![日本語入力を支える技術](http://ws.assoc-amazon.jp/widgets/q?_encoding=UTF8&Format=_SL110_&ASIN=4774149934&MarketPlace=JP&ID=AsinImage&WS=1&tag=takaoblogspot-22&ServiceVersion=20070822)](http://www.amazon.co.jp/gp/product/4774149934/ref=as_li_ss_il?ie=UTF8&tag=takaoblogspot-22&linkCode=as2&camp=247&creative=7399&creativeASIN=4774149934)

多くのエンジニアがこの本に触発され、各種アルゴリズムの理解を深めたり、一から勉強を始めたり、
また中にはこれを機に様々なライブラリを実装し公開する人も出てきました。

Trie4Jもそういったライブラリの一つで、各種トライ構造にターゲットを絞り、本書やその分野のブログなどを参考に実装されています。
現在以下のクラスがあります。

* パトリシアトライ
    * シンプルなパトリシアトライ(サイズ最適化無し)
        * [org.trie4j.patricia.simple.PatriciaTrie](https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/patricia/simple/PatriciaTrie.java)
    * 多層パトリシアトライ(接尾辞を格納するトライを内包しサイズを最適化。参考: [多層トライの実験結果 - やた＠はてな日記](http://d.hatena.ne.jp/s-yata/20101223/1293143633) )
        * [org.trie4j.patricia.multilayer.MultilayerPatriciaTrie](https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/patricia/multilayer/MultilayerPatriciaTrie.java)
    * Tail配列付きパトリシアトライ(ラベルをTail配列に格納)
        * [org.trie4j.patricia.tail.TailPatriciaTrie](https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/patricia/tail/TailPatriciaTrie.java)
* ダブルアレイ(又はダブル配列)
    * シンプルなダブルアレイ(サイズ最適化無し)
        * [org.trie4j.doublearray.DoubleArray](https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/doublearray/DoubleArray.java)
    * TAIL配列付きダブルアレイ(子が一つだけのノードが連続する場合に文字列としてTAIL配列に格納)
        * [org.trie4j.doublearray.TailDoubleArray](https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/doublearray/TailDoubleArray.java)
* LOUDS(Level-order unary degree structure) 簡潔 Trie
    * LOUDS簡潔トライ(TAIL配列付き)
        * [org.trie4j.louds.LOUDSTrie](https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/louds/LOUDSTrie.java)

以下のクラスは実験的実装です。trie4j-SNAPSHOT.jarには含まれません。

* ダブルアレイ(又はダブル配列)
    * TAIL配列付き最適化ダブルアレイ(未使用領域の開放やcheck配列をshortにした。実装は完了していますが、大規模なデータ(数万レコード超)には対応できません。)
        * [org.trie4j.doublearray.OptimizedTailDoubleArray](https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/doublearray/OptimizedTailDoubleArray.java)

**Tail配列の実装は、単に文字列を連結するもの(ConcatTailBuilder)と末尾トライを使ってサイズを圧縮したもの(SuffixTrieTailBuilder, デフォルト)の2つから選べます。**

