/*
 * Copyright 2012 Takao Nakaguchi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.trie4j.patricia.multilayer.labeltrie;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.trie4j.Algorithms;
import org.trie4j.Node;
import org.trie4j.NodeVisitor;
import org.trie4j.util.CharsUtil;

public class LabelNodeTest {
	public static void main(String[] args) throws Exception{
		String[] words = {
/*
				"apple", "appear", "a", "orange"
				, "applejuice", "appletea", "appleshower"
				, "orangejuice"
/*/
				"page_title",
				"!",
				"!!",
				"!!!",
				"!!!Fuck_You!!!",
				"!?",
				"!LOUD!",
				"!SHOUT!",
				"!_-attention-",
				"!wagero!",
				"\"",
				"\"74ers\"_LIVE_IN_OSAKA-JO_HALL_2003",
//*/

				};
		Map<String, LabelNode> nodes = new HashMap<String, LabelNode>();
		LabelNode root = new LabelNode(new char[]{});
		for(String w : words){
			System.out.println("--insert [" + w + "]--");
			nodes.put(w, root.insertChild(0, CharsUtil.revert(w.toCharArray()), 0));
			System.out.println("--dump--");
			root.visit(new NodeVisitor() {
				@Override
				public boolean visit(Node node, int nest) {
					for(int i = 0; i < nest; i++){
						System.out.print(" ");
					}
					if(node.getLetters().length > 0){
						System.out.print(node.getLetters());
					} else{
						System.out.print("<empty>");
					}
					System.out.println();
					return true;
				}
			}, 0);
		}
		for(String w : words){
			System.out.print(w + ": ");
			LabelNode node = nodes.get(w);
			while(node != null){
				System.out.print(CharsUtil.revert(node.getLetters()));
				System.out.print(" ");
				node = node.getParent();
			}
			System.out.println();
		}
		
		char[][] charss = {
				{'!', '!', (char)-1},
				{'!', (char)-1},
				{'p', 'a', 'g', 'e', '_', 't', 'i', 't', 'l', 'e', (char)-1}
		};
		for(char[] c : charss){
			System.out.println("--insert [" + new String(c) + "]--");
			LabelNode n = root.insertChild(0, CharsUtil.revert(c), 0);
			Algorithms.dump(root);
			System.out.println("--containsBottomup: " + n.containsBottomup(Arrays.copyOf(
					c, c.length - 1), 0));
		}
	}
}
