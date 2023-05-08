package org.example;

import java.util.ArrayList;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        // Press Alt+Enter with your caret at the highlighted text to see how
        // IntelliJ IDEA suggests fixing it.
        String x=  "{\n Hello World\n \n  \n \n}";

        x= x.replaceAll(" ","");
        x= x.replaceAll("\\{","");
        x= x.replaceAll("}","");
        ArrayList<String>y= new ArrayList<String>();
        for (String a:x.strip().split("\n")){
            //System.out.println(a);
            if (!a.isEmpty()){
                y.add(a);
            }
        }
        System.out.println(y.size());
    }
}


//because your keyboard is fucked, here is some of the = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
// and also this ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//and this }}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}