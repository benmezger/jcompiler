package compiler.parser;

import java.util.HashSet;
import java.util.Iterator;

public class RecoverySet extends HashSet {
    public RecoverySet() {
        super();
    }

    public RecoverySet(int t){
        this.add(t);
    }

    public boolean contains(int t){
        return super.contains(t);
    }

    public RecoverySet union(RecoverySet s){
        RecoverySet t = null;
        if (s != null){
           t = (RecoverySet) this.clone();
           this.addAll(s);
        }
        return t;
    }

    public RecoverySet remove(int n){
        RecoverySet t = (RecoverySet) this.clone();
        t.remove(n);
        return t;
    }

    public String toString(){
        Iterator iter = this.iterator();
        String s = "";
        int k;

        while (iter.hasNext()){
            k = (int) iter.next();
            s += LanguageParser.im(k) + " ";
        }
        return s;

    }

}
