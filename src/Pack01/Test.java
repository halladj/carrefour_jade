

package Pack01;

public class Test {
    public static void main(String[] args) {
            String[] commande = new String[3];
            String argument="";
            //                                     sx,xy,dx,dy,ID,Neighbots...
            argument = argument+"v1:Pack01.Vehicule(6,4,19,18,1,v2,v3,v4)";
            // argument = argument+"v1:Pack01.Vehicule(2,7,19,18,1,v2)";

            //                                     sx,xy,dx,dy,ID,Neighbots...
            argument = argument+";v2:Pack01.Vehicule(6,19,2,2,2,v1,v3,v4)";

            argument = argument+";v3:Pack01.Vehicule(2,15,17,3,3,v1,v2,v4)";
            argument = argument+";v4:Pack01.Vehicule(0,7,19,7,4,v1,v2,v3)";
            commande[0] = "-cp";
            commande[1] = "jade.boot";
            commande[2] = argument;
            jade.Boot.main(commande);

    }
}