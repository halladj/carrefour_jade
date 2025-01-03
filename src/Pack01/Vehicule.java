package Pack01;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class Vehicule extends Agent {
    // the grid.
    String[][] carte = new String[20][20];

    // starting position     : sx, sy -> (x,y)
    // destrination position : dx, dy -> (x,y)
    int sx, sy, dx, dy;
    String chemin = "";


    int id, h=0, last;
    String etat="dehors";
    ArrayList<String> Ri = new ArrayList<String>();
    ArrayList<String> attendu = new ArrayList<String>();
    ArrayList<String> differe = new ArrayList<String>();

    @Override
    protected void setup() {

        // R&A setup part-2....
        // addBehaviour(new enDehorsSc());
        addBehaviour(new consulterBoite());

        // sets up the starting and destination points.
        addBehaviour(new obtenir_route());

        // sets up R&A algo.
        addBehaviour(new set_up());
        // sets up the grid.
        addBehaviour(new obtenir_carte());

        // desribes how should the veicle move to reach its destination.
        addBehaviour(new traiter_route());


    }

    

    public class obtenir_route extends OneShotBehaviour {
        @Override
        public void action() {
            Object[] args = getArguments();
            sx = Integer.parseInt((String) args[0]);
            sy = Integer.parseInt((String) args[1]);
            dx = Integer.parseInt((String) args[2]);
            dy = Integer.parseInt((String) args[3]);
        }
    }

    public class set_up extends OneShotBehaviour{

        @Override
        public void action() {
            // Ricart and Agrawala variables
            Object [] args = getArguments();
            if (args != null) {
                id=Integer.parseInt(args[4].toString());
                for (int i=5;i<args.length;i++) {
                    Ri.add(args[i].toString());
                }
                System.out.println("Agent " +getLocalName() +", Mes arguments : id="+id+", Ri="+ Ri.toString());
            }
            System.out.println("Agent " + getLocalName() + " est en dehors de Sc");
        }

    }

    public class obtenir_carte extends OneShotBehaviour {
        @Override
        public void action() {
            for (int i = 0; i < 20; i++) {
                for (int j = 0; j < 20; j++) {
                    carte[i][j] = " ";
                }
            }
            for (int j = 0; j < 20; j++) {
                carte[2][j] = "R";
                carte[6][j] = "R";
            }
            for (int i = 0; i < 20; i++) {
                if (carte[i][7]=="R")
                    carte[i][7] = "C";
                else
                    carte[i][7] = "R";
            }
            for (int i = 6; i < 20; i++) {
                if (carte[i][18]=="R")
                    carte[i][18] = "C";
                else
                    carte[i][18] = "R";
            }
            for (int j = 0; j < 20; j++) {
                if (carte[17][j]=="R")
                    carte[17][j] = "C";
                else
                    carte[17][j] = "R";
            }
            for (int j = 0; j < 8; j++) {
                if (carte[10][j]=="R")
                    carte[10][j] = "C";
                else
                    carte[10][j] = "R";
            }
        }
    }

    // Algo used to determine how does the car more.
    public class traiter_route extends OneShotBehaviour {
        @Override
        public void action() {
            if (sx == dx) {
                if (sy == dy) {
                    chemin = "S=D";
                } else {
                    if (sy < dy) {
                        // deplace droit <move right>.
                        chemin = "dd";
                    } else {
                        // deplace gauche <move left>.
                        chemin = "dg";
                    }
                }
            } else {
                if (sy == dy) {
                    if (sx < dx) {
                        // deplace bas<move down> cz 0 is up and 19 is bottom.
                        chemin = "db";
                    } else {
                        // deplace haut <move up>.
                        chemin = "dh";
                    }
                } else {
                    // can't find destination, and find a new starting point (sx, sy).
                    chemin = "chercher";
                }
            }
            System.out.println("Vehicule: " + getLocalName() + " part de: (" + sx + "," + sy + ")" + " et arrive à: (" + dx + "," + dy + ")" + " chemin= " + chemin);
            switch (chemin) {
                case "S=D":
                    System.out.println("!!!!! La source est elle meme la destination pour le vehicule " + getLocalName());
                    break;
                case "dd":
                    addBehaviour(new depldroite());
                    break;
                case "dg":
                    addBehaviour(new deplgauche());
                    break;
                case "dh":
                    addBehaviour(new deplhaut());
                    break;
                case "db":
                    addBehaviour(new deplbas());
                    break;
                case "chercher":
                    addBehaviour(new envoyerRequete());
                    // addBehaviour(new deplcar());
                    break;
            }
        }
    }

    // class enDehorsSc extends OneShotBehaviour {
    //     @Override
    //     public void action() {
    //         String var1 = "v" + Integer.toString(((int)(Math.random() * 2))+1);
    //         String var2 = "v" + Integer.toString(((int)(Math.random() * 2))+1);
    //         if ((getLocalName().equals(var1)) || (getLocalName().equals(var2))) {
    //             addBehaviour(new envoyerRequete());
    //         }else {
    //             block(5000);
    //             addBehaviour(new enDehorsSc());
    //         }
    //     }
    // }

    // Nothing to be changed.
    class envoyerRequete extends OneShotBehaviour {
        @Override
        public void action() {
                etat = "demandeur";
                h++;
                last = h;
                for (int i = 0; i < Ri.size(); i++) {
                    attendu.add(Ri.get(i));
                }
                for (int j = 0; j < Ri.size(); j++) {
                    ACLMessage msgEnvoi = new ACLMessage(ACLMessage.INFORM);
                    msgEnvoi.addReceiver(new AID(Ri.get(j), AID.ISLOCALNAME));
                    try {
                        msgEnvoi.setContentObject(new Message("requete", last, id));
                        send(msgEnvoi);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("Agent " + getLocalName() + " a envoye une requete (" + last + "," + id + ")");
                addBehaviour(new attendrePermission());
        }
    }

    // Nothing to be changed.
    class attendrePermission extends Behaviour {
        @Override
        public void action() {
            if (attendu.isEmpty() && etat.equals("demandeur")) {
                etat="dedans";
                addBehaviour(new enSC());
            }
        }

        @Override
        public boolean done() {
            if (attendu.isEmpty()) return true;
            return false;
        }
    }   

    public class enSC extends OneShotBehaviour {
        @Override
        public void action() {
            System.out.println("============Agent " +getLocalName()+" En Sc");
            addBehaviour(new deplcar());
            // TODO: put the code of carffeure here
        }
    }

    public class libererSc extends OneShotBehaviour {
        @Override
        public void action() {
            etat="dehors";
            for (int j=0;j < differe.size();j++) {
                ACLMessage msgEnvoi = new ACLMessage(ACLMessage.INFORM);
                msgEnvoi.addReceiver(new AID(differe.get(j), AID.ISLOCALNAME));
                try {
                    System.out.println("============Agent " +getLocalName()+" Libere Sc");
                    msgEnvoi.setContentObject(new Message("permission",id));
                    send(msgEnvoi);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            differe.removeAll(differe);
            addBehaviour(new traiter_route());
            // block(30000);
            // addBehaviour(new enDehorsSc());
        }
    }
    
    public class consulterBoite extends CyclicBehaviour {
        @Override
        public void action() {
            ACLMessage msgRecu = receive();
            if (msgRecu != null) {
                try {
                Object c= msgRecu.getContentObject();
                if (c instanceof Message) {
                    Message contenu = (Message) c;
                    if (contenu.type.equals("requete")) {
                        int k = contenu.last;
                        int j = contenu.id;
                        h=Math.max(h,k);
                        boolean priorite = ((etat.equals("dedans")) || (etat.equals("demandeur") && ((last<k) || ((last==k)&& (id<j)))));
                        if (priorite) {
                            differe.add(msgRecu.getSender().getLocalName());
                        }
                        else {
                            ACLMessage msgEnvoi = new ACLMessage(ACLMessage.INFORM);
                            msgEnvoi.addReceiver(msgRecu.getSender());
                            msgEnvoi.setContentObject(new Message("permission",id));
                            send(msgEnvoi);
                        }
                    }
                    else {
                        if (contenu.type.equals("permission")) {
                            attendu.remove(msgRecu.getSender().getLocalName());
                        }
                    }
                }
                } catch (UnreadableException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class depldroite extends Behaviour {
        int ind = 0, oldsy;

        @Override
        public void onStart() {
            int oldsy = sy;
        }

        @Override
        public void action() {
            if ((carte[sx][sy + 1].equals("R") || (carte[sx][sy + 1].equals("C"))) && (sy + 1 <= carte[sx].length)) {
                sy++;
                System.out.println("Vehicule: " + getLocalName() + " se deplace a: (" + sx + "," + sy + ")");
            } else { // Pas de chemin direct a droite vers la destination pour le Vehicule
                sy = dy;
                ind = 1;
            }
        }

        @Override
        public boolean done() {
            if (sy == dy) {
                if (ind == 0)
                    System.out.println("Vehicule: " + getLocalName() + " arrive a destination : (" + sx + "," + sy + ")");
                else {
                    sy = oldsy;
                    addBehaviour(new deplcar());
                }
                return true;
            } else return false;
        }
    }

    public class deplgauche extends Behaviour {
        int ind = 0, oldsy;

        public void onStart() {
            oldsy = sy;
        }

        @Override
        public void action() {
            if ((carte[sx][sy - 1].equals("R") || (carte[sx][sy - 1].equals("C"))) && (sy - 1 >= 0)) {
                sy--;
                System.out.println("Vehicule: " + getLocalName() + " se deplace a: (" + sx + "," + sy + ")");
            } else {// Pas de chemin direct a gauche vers la destination pour le Vehicule
                sy = dy;
                ind = 1;
            }
        }

        @Override
        public boolean done() {
            if (sy == dy) {
                if (ind == 0)
                    System.out.println("Vehicule: " + getLocalName() + " arrive a destination : (" + sx + "," + sy + ")");
                else {
                    sy = oldsy;
                    addBehaviour(new deplcar());
                }
                return true;
            } else return false;
        }
    }

    public class deplhaut extends Behaviour {
        int ind = 0, oldsx;

        @Override
        public void onStart() {
            oldsx = sx;
        }

        @Override
        public void action() {
            if ((carte[sx - 1][sy].equals("R") || (carte[sx - 1][sy].equals("C"))) && (sx - 1 >= 0)) {
                sx--;
                System.out.println("Vehicule: " + getLocalName() + " se deplace a: (" + sx + "," + sy + ")");
            } else {// Pas de chemin direct en haut vers la destination pour le Vehicule
                sx = dx;
                ind = 1;
            }
        }

        @Override
        public boolean done() {
            if (sx == dx) {
                if (ind == 0)
                    System.out.println("Vehicule: " + getLocalName() + " arrive a destination : (" + sx + "," + sy + ")");
                else {
                    sx = oldsx;
                    addBehaviour(new deplcar());
                }
                return true;
            } else return false;
        }
    }

    public class deplbas extends Behaviour {
        int ind = 0, oldsx;

        @Override
        public void onStart() {
            oldsx = sx;
        }

        @Override
        public void action() {
            if ((carte[sx + 1][sy].equals("R") || (carte[sx + 1][sy].equals("C"))) && (sx + 1 <= carte[sx].length)) {
                sx++;
                System.out.println("Vehicule: " + getLocalName() + " se deplace a: (" + sx + "," + sy + ")");
            } else {// Pas de chemin direct en bas vers la destination pour le Vehicule
                sx = dx;
                ind = 1;
            }
        }

        @Override
        public boolean done() {
            if (sx == dx) {
                if (ind == 0)
                    System.out.println("Vehicule: " + getLocalName() + " arrive a destination : (" + sx + "," + sy + ")");
                else {
                    sx = oldsx;
                    addBehaviour(new deplcar());
                }
                return true;
            } else return false;
        }
    }

    public class deplcar extends OneShotBehaviour {
        int pas;

        @Override
        public void onStart() {
            pas = 1;
        }

        @Override
        public void action() {
            int value = ((int)(Math.random()*4))+1;
            switch (value) {
                case 1 : rechCar1();
                    break;
                case 2 : rechCar2();
                    break;
                case 3 : rechCar3();
                    break;
                case 4 : rechCar4();
                    break;
            }
            System.out.println("===========Vehicule: " + getLocalName() + " au carreffour: (" + sx + "," + sy + ")");
            addBehaviour(new libererSc());
        }

        protected int rechCarD(int cor) {
            pas = 1;
            if ((cor + pas) < (carte[sx].length)) {
                while (((cor + pas) < (carte[sx].length)) && (carte[sx][cor + pas].equals("R"))) {
                    pas++;
                }
                if ((cor + pas) < carte[sx].length) {
                    if (carte[sx][cor + pas].equals("C")) {
                        return pas;
                    } else {
                        return -1;
                    }
                } else {
                    return -1;
                }

            } else {
                return -1;
            }
        }

        protected int rechCarH(int cor) {
            pas = 1;
            if ((cor-pas) >= 0) {
                while (((cor-pas) >= 0) && (carte[cor-pas][sy].equals("R"))) {
                    pas++;
                }
                if ((cor-pas) >= 0) {
                    if (carte[cor-pas][sy].equals("C")) {
                        return pas;
                    } else {
                        return -1;
                    }

                } else {
                    return -1;
                }
            } else {
                return -1;
            }
        }

        protected int rechCarG(int cor) {
            pas = 1;
            if ((cor - pas) >= 0) {
                while (((cor-pas) >= 0) && (carte[sx][cor-pas].equals("R"))) {
                    pas++;
                }
                if ((cor - pas) >= 0) {
                    if (carte[sx][cor-pas].equals("C")) {
                        return pas;
                    } else {
                        return -1;
                    }

                } else {
                    return -1;
                }
            } else {
                return -1;
            }
        }

        protected int rechCarB(int cor) {
            pas = 1;
            if ((cor + pas) < (carte[sx].length)) {
                while (((cor + pas) < (carte[sx].length)) && (carte[cor + pas][sy].equals("R"))) {
                    pas++;
                }
                if ((cor + pas) < carte[sx].length) {
                    if (carte[cor+pas][sy].equals("C")) {
                        return pas;
                    } else {
                        return -1;
                    }
                } else {
                    return -1;
                }
            } else {
                return -1;

            }
        }
        protected void rechCar1() {

                int v = rechCarD(sy);
                if (v == -1) {
                    v = rechCarH(sx);
                    if (v == -1) {
                        v = rechCarG(sy);
                        if (v == -1) {
                            v = rechCarB(sx);
                            if (v == -1) {

                            } else {
                                for (int i = 1; i <= v; i++) {
                                    sx++;
                                }
                            }
                        } else {
                            for (int i = 1; i <= v; i++) {
                                sy--;
                            }
                        }
                    } else {
                        for (int i = 1; i <= v; i++) {
                            sx--;
                        }
                    }

                } else {
                    for (int i = 1; i <= v; i++) {
                        sy++;
                    }
                }
        }


        protected void rechCar2() {
                int v = rechCarH(sx);
                if (v == -1) {
                    v = rechCarG(sy);
                    if (v == -1) {
                        v = rechCarB(sx);
                        if (v == -1) {
                            v = rechCarD(sy);
                            if (v == -1) {

                            } else {
                                for (int i = 1; i <= v; i++) {
                                    sy++;
                                }
                            }
                        } else {
                            for (int i = 1; i <= v; i++) {
                                sx++;
                            }
                        }
                    } else {
                        for (int i = 1; i <= v; i++) {
                            sy--;
                        }
                    }

                } else {
                    for (int i = 1; i <= v; i++) {
                        sx--;
                    }
                }
        }

        protected void rechCar3() {
            int v = rechCarG(sy);
            if (v == -1) {
                v = rechCarB(sx);
                if (v == -1) {
                    v = rechCarD(sy);
                    if (v == -1) {
                        v = rechCarH(sx);
                        if (v == -1) {

                        } else {
                            for (int i = 1; i <= v; i++) {
                                sx--;
                            }
                        }
                    } else {
                        for (int i = 1; i <= v; i++) {
                            sy++;
                        }
                    }
                } else {
                    for (int i = 1; i <= v; i++) {
                        sx++;
                    }
                }

            } else {
                for (int i = 1; i <= v; i++) {
                    sy--;
                }
            }
        }

        protected void rechCar4() {
            int v = rechCarB(sx);
            if (v == -1) {
                v = rechCarD(sy);
                if (v == -1) {
                    v = rechCarH(sx);
                    if (v == -1) {
                        v = rechCarG(sy);
                        if (v == -1) {
                        } else {
                            for (int i = 1; i <= v; i++) {
                                sy--;
                            }
                        }
                    } else {
                        for (int i = 1; i <= v; i++) {
                            sx--;
                        }
                    }
                } else {
                    for (int i = 1; i <= v; i++) {
                        sy++;
                    }
                }

            } else {
                for (int i = 1; i <= v; i++) {
                    sx++;
                }
            }
        }
    }

    
}
