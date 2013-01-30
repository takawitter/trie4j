<html>
<head>
<meta name="author" content="Takao Nakaguchi" />
<meta name="description" content="Trie4J - various trie implementation for Java" />
<meta name="keywords" content="Trie,Java,DoubleArray,Double Array,LOUDS,PATRICIA" />
</head>
# Trie4J - various trie implementation for Java.

Trie4J is the sort of collection of varios trie implementation.

[![Build Status](https://buildhive.cloudbees.com/job/takawitter/job/trie4j/badge/icon)](https://buildhive.cloudbees.com/job/takawitter/job/trie4j/)
 **latest [trie4j-SNAPSHOT.jar](https://buildhive.cloudbees.com/job/takawitter/job/trie4j/lastSuccessfulBuild/artifact/trie4j/dist/trie4j-SNAPSHOT.jar)**

---
### Performance comparison:
with 1.27 million words and 10.04 million chars contained in jawiki-20120220-all-titles-in-ns0.gz .
<br/>on MacOS X(10.7), Core i7 2.5GHz, Java 6.
<table>
<tr><th colspan="2">class</th><th>notes</th><th>build(ms)</th><th>contains(ms)</th><th>used heap(MB)</th></tr>
<tr><td colspan="2">java.util.HashSet</td><td /><td align="right">333<sup>*1</sup></td><td align="right">450</td><td align="right">135.2</td></tr>
<tr><td colspan="2">java.util.TreeSet</td><td /><td align="right"><font color="red">410</font><sup>*1</sup></td><td align="right">263</td><td align="right">137.1</td></tr>
<tr>
  <td colspan="2">PatriciaTrie(<a href="https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/patricia/simple/PatriciaTrie.java">src</a>)</td>
  <td>Simple PATRICIA Trie.</td><td align="right">522<sup>*1</sup></td><td align="right">245</td><td align="right">91.8</td>
</tr>
<tr>
  <td rowspan="2">TailPatriciaTrie(<a href="https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/patricia/tail/TailPatriciaTrie.java">src</a>)</td>
  <td>SuffixTrieTailBuilder(<a href="https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/tail/SuffixTrieTailBuilder.java">src</a>)</td>
  <td rowspan="2">PATRICIA Trie with tail string.</td>
  <td align="right">1,074<sup>*1</sup></td><td align="right">281</td><td align="right">80.8</td></tr>
<tr>
  <td>ConcatTailBuilder(<a href="https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/tail/ConcatTailBuilder.java">src</a>)</td>
  <td align="right">486<sup>*1</sup></td><td align="right">245</td><td align="right">70.1</td>
</tr>
<tr>
  <td colspan="2">DoubleArray(<a href="https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/doublearray/DoubleArray.java">src</a>)</td>
  <td>Simple Double Array Trie.</td>
  <td align="right">393<sup>*2</sup></td><td align="right"><font color="red">106</a></td><td align="right">49.0</td>
</tr>
<tr>
  <td rowspan="2">TailDoubleArray(<a href="https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/doublearray/TailDoubleArray.java">src</a>)</td>
  <td>SuffixTrieTailBuilder(<a href="https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/tail/SuffixTrieTailBuilder.java">src</a>)</td>
  <td rowspan="2">Double Array Trie with tail string.</td>
  <td align="right">3,126<sup>*2</sup></td><td align="right">189</td><td align="right">29.9</td>
</tr>
<tr>
  <td>ConcatTailBuilder(<a href="https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/tail/ConcatTailBuilder.java">src</a>)</td>
  <td align="right">2,618<sup>*2</sup></td><td align="right">162</td><td align="right">34.6</td>
</tr>
<tr>
  <td rowspan="2">LOUDSTrie(<a href="https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/louds/LOUDSTrie.java">src</a>)</td>
  <td>SuffixTrieTailBuilder(<a href="https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/tail/SuffixTrieTailBuilder.java">src</a>)</td>
  <td rowspan="2">LOUDS Succinct Trie with tail string.</td>
  <td align="right">682<sup>*2</sup></td><td align="right">524</td><td align="right"><font color="red">16.3</a></td>
</tr>
<tr>
  <td>ConcatTailBuilder(<a href="https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/tail/ConcatTailBuilder.java">src</a>)</td>
  <td align="right"><font color="red">191</font><sup>*2</sup></td><td align="right">501</td><td align="right">21.0</td>
</tr>
<tr>
  <td rowspan="2">LOUDSPPTrie(<a href="https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/louds/LOUDSPPTrie.java">src</a>)<sup>*3</sup></td>
  <td>SuffixTrieTailBuilder(<a href="https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/tail/SuffixTrieTailBuilder.java">src</a>)</td>
  <td rowspan="2">LOUDS++ Succinct Trie with tail string.</td>
  <td align="right">725<sup>*2</sup></td><td align="right">562</td><td align="right"><font color="red">16.3</a></td>
</tr>
<tr>
  <td>ConcatTailBuilder(<a href="https://github.com/takawitter/trie4j/blob/master/trie4j/src/org/trie4j/tail/ConcatTailBuilder.java">src</a>)</td>
  <td align="right"><font color="red">234</font><sup>*2</sup></td><td align="right">534</td><td align="right">21.0</td>
</tr>
</table>
*1 - build from string array.
<br/>*2 - build from other trie(org.trie4j.patricia.simple.PatriciaTrie).
<br/>*3 - under memory optimization (not yet well optimized).

---

### Sample codes:
```java
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
```

---

### additional notes.

These classes are experimental and not contained in trie4j-SNAPSHOT.jar.
* Multilayer Patricia Trie ([src](https://github.com/takawitter/trie4j/blob/master/trie4j/src.kitchensink/org/trie4j/patricia/multilayer/MultilayerPatriciaTrie.java))
 * optimizes size using Multilayer Trie but no significant improvement.
* DoubleArray Array with Tail Array and some optimization ([src](https://github.com/takawitter/trie4j/blob/master/trie4j/src.kitchensink/org/trie4j/doublearray/OptimizedTailDoubleArray.java))
 * feature completed but can't support large data (over several 10 thoudsants).

---

### additional notes(ja).

2012年2月、1冊の本が発売されました。

"日本語入力を支える技術" 変わり続けるコンピュータと言葉の世界 (WEB+DB PRESS plus) 徳永 拓之 (著) 

 [![日本語入力を支える技術](http://ws.assoc-amazon.jp/widgets/q?_encoding=UTF8&Format=_SL110_&ASIN=4774149934&MarketPlace=JP&ID=AsinImage&WS=1&tag=takaoblogspot-22&ServiceVersion=20070822)](http://www.amazon.co.jp/gp/product/4774149934/ref=as_li_ss_il?ie=UTF8&tag=takaoblogspot-22&linkCode=as2&camp=247&creative=7399&creativeASIN=4774149934)

多くのエンジニアがこの本に触発され、各種アルゴリズムの理解を深めたり、一から勉強を始めたり、
また中にはこれを機に様々なライブラリを実装し公開する人も出てきました。trie4jもそういったライブラリの一つで、各種トライ構造にターゲットを絞り、本書やその分野のブログなどを参考に実装されています。

ほとんどのクラスはシンプルな実装になっていますが、一部独自の最適化が入っています。また、各トライが提供するメソッドは、
極力中間オブジェクトを作らないようになっており、オブジェクト生成/破棄によるパフォーマンス低下を起こさないよう実装されています。

下記クラスは実験的実装で、trie4j-SNAPSHOT.jarには含まれません(src.kitchensinkにあります)。
* 多層パトリシアトライ(MultilayerPatriciaTrie([src](https://github.com/takawitter/trie4j/blob/master/trie4j/src.kitchensink/org/trie4j/patricia/multilayer/MultilayerPatriciaTrie.java)))
 * [多層トライの実験結果 - やた＠はてな日記](http://d.hatena.ne.jp/s-yata/20101223/1293143633)
   を参考に、接尾辞を格納するトライを内包しサイズを最適化した実装です。また、子を持たないノード、子を一つだけ持つノード、それぞれの終端/非終端版と、様々な種類のノードを用意して
   使い分けることで、極力無駄なメモリを使わないようにしています。但しパトリシアトライのままなので、あまり効率が上がっていません。

* TAIL配列付き最適化ダブルアレイ(OptimizedTailDoubleArray([src](https://github.com/takawitter/trie4j/blob/master/trie4j/src.kitchensink/org/trie4j/doublearray/OptimizedTailDoubleArray.java)))
    * 未使用領域の開放やcheck配列をshortにした。実装は完了していますが、大規模なデータ(数万レコード超)には対応できません。

</html>
