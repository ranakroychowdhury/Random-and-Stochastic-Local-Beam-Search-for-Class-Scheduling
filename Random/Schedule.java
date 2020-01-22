import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


/**
 * Created by Ranakrc on 10-Nov-17.
 */
public class Schedule {
    public static int teachers, rooms, classes, periods = 30, requirements, k = 4;

    //maintain the list of least costs among the children
    public static int[] kElements = new int[k];

    //stores all the individual elements
    public static int[] sched;

    //weight of each metric: name room, teacher and class
    public static int[] weight = {1, 1, 1};

    //maintains a list of the current states
    public static ArrayList<State> initStates = new ArrayList<>();

    //maintains a list of the cost of the current k states
    public static int[] globalCost = new int[k];


    //print each element
    public static void printElement(int a, int j, int size) {

        int room = (a / (classes * teachers)) + 1;
        int temp = a - (room - 1) * classes * teachers;
        int teacher = (temp % teachers) + 1;
        int clas = (temp / teachers) + 1;
        if(j < size - 1)
            System.out.println("[" + room + ", " + teacher + ", " + clas + "]");
        else
            System.out.print("[" + room + ", " + teacher + ", " + clas + "]");
    }


    //prints the configuration of each state
    public static void printState(State node) {
        int i, j;
        for(i = 0; i < periods; i++) {
            int periodNo = i + 1;
            System.out.println("For Period " + periodNo + ": ");
            System.out.print("[ ");
            for(j = 0; j < node.outer[i].size(); j++) {
                //System.out.print(node.outer[i].get(j) + " ");
                printElement(node.outer[i].get(j), j, node.outer[i].size());
            }
            System.out.println("]");
        }
        System.out.println("Cost is: " + node.cost);
    }


    //find the total cost of either teacher, class or room in a period
    public static int findFreq(int[] a, int wt) {

        int var;

        if(wt == 0)
            var = rooms;
        else if(wt == 1)
            var = teachers;
        else
            var = classes;

        int[] freq = new int[var];
        boolean[] test = new boolean[var];
        int i, j;
        for (i = 0; i < a.length; i++) {
            int x = a[i];
            if(!test[x]) {
                for (j = 0; j < a.length; j++) {
                    if (x == a[j])
                        freq[x]++;
                }
                test[x] = true;
            }
        }

        int cost = 0;

        for (i = 0; i < freq.length; i++) {
            cost += Math.max(0, freq[i] - 1);
        }

        return weight[wt] * cost;
    }


    //calculate the cost associated with a period for either teacher, room or class
    public static int perPeriodCost(List<Integer> element) {
        int row = element.size();
        int column = 3, i, j;
        int[] room = new int[row];
        int[] teacher = new int[row];
        int[] clas = new int[row];

        for(i = 0; i < element.size(); i++) {
                room[i] = element.get(i) / (classes * teachers);
                int temp = element.get(i) - room[i] * classes * teachers;
                teacher[i] = temp % teachers;
                clas[i] = temp / teachers;

        }

        int cost = 0;
        cost += findFreq(room, 0);
        cost += findFreq(teacher, 1);
        cost += findFreq(clas, 2);

        return cost;
    }


    //calculate the cost associated with each state
    public static int detCost(State node) {

        int totalCost = 0;
        int i;

        for(i = 0; i < periods; i++)
            totalCost += perPeriodCost(node.outer[i]);

        return totalCost;
    }


    //make the initial states
    public static void makeInitState(int[] inp) {

        int i, j, l;
        int quot = requirements / periods;
        int rem = requirements % periods;

        int periodFreq;
        if(rem == 0)
            periodFreq = quot;
        else
            periodFreq = quot + 1;

        for(i = 0; i < k; i++) {
            State state = new State();
            state.initialize(periods, periodFreq);
            int num = 0;
            for(j = 0; j < periods; j++) {
                for(l = 0; l < periodFreq ; l++) {
                    if (num < requirements)
                        state.outer[j].add(sched[num]);
                    else
                        continue;
                    num++;
                }
            }
            state.cost = detCost(state);
            globalCost[i] = state.cost;
            kElements[i] = state.cost;
            initStates.add(state);
            //System.out.println("For k = " + i);
            //printState(initStates.get(i));
            //System.out.println("COST: " + state.cost);
            periodFreq++;
        }

    }


    //converts the input into individual elements
    public static void test(int[] inp) {

        int i, j, req = 0;
        for(i = 0; i < inp.length; i++) {
            for (j = 0; j < inp[i]; j++) {
                sched[req] = i;
                req++;
            }
        }

        /*for(i = 0; i <requirements; i++)
            System.out.println(sched[i]);*/
    }


    //Given the cost, this function returns the state corresponding to this cost
    public static State getOb(ArrayList<State> sub, int cost) {
        int i, j;
        for(i = 0; i < sub.size(); i++) {
            if(cost == sub.get(i).cost)
                return sub.get(i);
        }
        return null;
    }


    //Returns the maximum element of an array
    public static int getMax(ArrayList<State> arr) {
        int max = arr.get(0).cost;
        int i;
        for(i = 0; i < arr.size(); i++) {
            if(arr.get(i).cost > max)
                max = arr.get(i).cost;
        }
        return max;
    }


    //Returns the index of the minimum element of an array
    public static int getMin(ArrayList<State> arr) {
        int min = arr.get(0).cost;
        int i, index = 0;
        for(i = 0; i < arr.size(); i++) {
            if(arr.get(i).cost < min) {
                min = arr.get(i).cost;
                index = i;
            }
        }
        return index;
    }


    //Finding k elements with the smallest costs
    public static void smallestStates(ArrayList<State> node) {
        int i;
        int[] array = new int[node.size()];
        for(i = 0; i < node.size(); i++)
            array[i] = node.get(i).cost;

        Arrays.sort(array);

        for(i = 0; i < k; i++) {
            kElements[i] = array[i];
        }
    }


    //find whether an element in an array has changed
    public static boolean change() {
        int i, j;
        boolean t;
        for(i = 0; i < k; i++) {
            int a = kElements[i];
            int count = 0;
            for(j = 0; j < k; j++) {
                if(a != globalCost[j])
                    count++;
            }
            if(count == k)
                return true;
        }
        return false;
    }


    //Search Algorithm
    public static State localBeamSearch() {

        //System.out.println("Inside LBS");
        int count = 0;
        while(change() || count == 0) {
            ArrayList<State> childStates = new ArrayList<>();
            count++;
            //System.out.println("Inside while: " + count);
            int i, j, l, m;
            //copy the current states to the childStates
            for(i = 0; i < k; i++) {
                childStates.add(initStates.get(i));
                globalCost[i] = kElements[i];
            }

            for(i = 0; i < k; i++) {

                //got a state
                //generate all the child states of each of the current states
                for(j = 0; j < periods; j++) {

                    //got a particular row corresponding to a state
                    for(l = 0; l < initStates.get(i).outer[j].size(); l++) {

                        //got a particular element
                        //now generate all child substates by placing this element
                        //in all the remaining periods
                        for(m = 0; m < periods; m++) {

                            if(m != j) {
                                State temp = new State();
                                temp.initialize(initStates.get(i).number, initStates.get(i).perFreq);
                                temp.cpy(initStates.get(i).outer);
                                temp.outer[m].add(temp.outer[j].remove(l));
                                temp.cost = detCost(temp);
                                if(temp.cost < getMax(childStates)) {
                                    childStates.add(temp);
                                    childStates.remove(getOb(childStates, getMax(childStates)));
                                }
                            }
                        }
                    }
                }
            }

            smallestStates(childStates);
            for(i = 0; i < k; i++) {
                initStates.remove(i);
                initStates.add(getOb(childStates, kElements[i]));
                //printState(initStates.get(i));
            }
        }
        return initStates.get(getMin(initStates));
    }


    public static void main(String args[]) throws FileNotFoundException, UnsupportedEncodingException {

        Scanner scan = null;
        int[] num = new int[5];
        File f = new File("hdtt8note.txt");
        try {
            scan = new Scanner(f);
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            System.exit(0);
        }

        int j = 0;
        while (scan.hasNextLine()) { //Note change
            String currentLine = scan.nextLine();
            //split into words
            String words[] = currentLine.split(" ");

            //For each word in the line
            for(String str : words) {
                try {
                    num[j] = Integer.parseInt(str);
                    j++;
                }catch(NumberFormatException nfe) { }; //word is not an integer, do nothing
            }
        } //end while

        // close the scanner
        scan.close();
        teachers = num[0];
        classes = num[2];
        rooms = num[3];
        requirements = num[4];
        sched =  new int[requirements];

        Scanner scanner = new Scanner(new File("hdtt8req.txt"));

        int[] input = new int[100000];
        int i = 0;
        while (scanner.hasNextInt()) {
            input[i] = scanner.nextInt();
            i++;
        }

        test(input);
        makeInitState(input);
        State goal = localBeamSearch();
        printState(goal);
    }
}

//The weight matrix, the number of periods and the number of initial random states, k, may still be varied.
