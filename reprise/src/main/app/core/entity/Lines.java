package app.core.entity;

import java.util.*;

public class Lines extends ArrayList<Object[]>{
    private static final int posID = 0;
    private static int[] posIndex;
    private static Object[] nameIndex;
    private static Object[] types;

    public Lines(int[] posIndex, Object[] nameIndex, List<Object[]> lines, Object[] types) {
        super();
        this.posIndex = posIndex;
        this.nameIndex = nameIndex;
        this.types = types;
        this.addAll(lines);
    }

    public Lines(List<Object[]> list) {
        super();
        this.addAll(list);
    }

    public int[] getPosIndex() {
        return posIndex;
    }

    public int getPosID() {
        return posID;
    }

    public Object[] getNameIndex() {
        return nameIndex;
    }

    public Object[] getTypes() {
        return types;
    }

    public int getPosNameIndex(Object name) {
        for (int i=0; i<nameIndex.length; i++) {
            if (name.equals(nameIndex[i])) {
                return i;
            }
        }
        return -1;
    }
    
    /********************************************************		insert		*/
    
    public void setLines(List<Object[]> lines) {
        this.clear();
        this.addAll(lines);
    }
    
    /********************************************************		find		*/

    public Lines getLines(List<Integer> ids) {
    	ArrayList<Object[]> res = new ArrayList<Object[]>();
    	Object[] line;
        for (Integer it : ids) {
            if ((line = rechercheDicho(it)) != null) {
                res.add(line);
            }
        }
        return new Lines(res);
    }

    public Lines getLinesWithSelect(List<Integer> ids, List<String> selection) {
        ArrayList<Object[]> res = new ArrayList<Object[]>();
        int acc = 0;
        Object[] line;
        Object[] selectLine = new Object[selection.size()];
        List<Integer> select = new ArrayList<>();
        for (String attribute : selection) {
            for (int i = 0; i < nameIndex.length; i++) {
                if (attribute.equals(nameIndex[i].toString())) {
                    select.add(i);
                    break;
                }
            }
        }
        for (Integer it : ids) {
            if ((line = rechercheDicho(it)) != null) {
                for (int x=0; x<select.size(); x++) {
                    selectLine[x] = line[select.get(x)];
                }
                res.add(selectLine);
            }
        }
        return new Lines(res);
    }



    //@IDEA peut etre rechercher a partir de sublines a chaque fois car ids donnés dans l'ordre
    public Object[] rechercheDicho(Integer val){

        /* déclaration des variables locales à la fonction */
        boolean trouve;
        int id;
        int ifin;
        int im;
        Integer tmp;

        /* initialisation de ces variables avant la boucle de recherche */
        trouve = false;
        id = 0;
        ifin = size();

        /* boucle de recherche */
        while(!trouve && ((ifin - id) > 1)){

            im = (id + ifin)/2;
            tmp = (Integer)get(im)[posID];
            trouve = (tmp == val);

            if(tmp > val) ifin = im;
            else id = im;
        }

        /* test conditionnant la valeur que la fonction va renvoyer */
        if(get(id)[posID] == val) return(get(id));
        else return null;

    }

    public ArrayList<Object[]> getLines(int pos, Object value) {
    	ArrayList<Object[]> res = new ArrayList<Object[]>();
    	for (Object[] line : this) {
    		if (line[pos].equals(value)) {
    			res.add(line);
    		}
    	}
    	return res;
    }

    /********************************************************		print		*/

    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (Object[] line : this) {
            sb.append("[");
            for (Object e : line) {
                sb.append(e.toString() + ", ");
            }
            sb.append("]\n");
        }
        return sb.toString();
    }
    
}
