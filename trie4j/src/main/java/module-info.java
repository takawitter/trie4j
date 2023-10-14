module org.trie4j {
    requires java.base;
    requires transitive java.management;
    requires transitive jdk.unsupported;

    exports org.trie4j;
    exports org.trie4j.bv;
    exports org.trie4j.doublearray;
    exports org.trie4j.io;
    exports org.trie4j.louds;
    exports org.trie4j.louds.bvtree;
    exports org.trie4j.patricia;
    exports org.trie4j.tail;
    exports org.trie4j.tail.builder;
    exports org.trie4j.tail.index;
    exports org.trie4j.util;
}
