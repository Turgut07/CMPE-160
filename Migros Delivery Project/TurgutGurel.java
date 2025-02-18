import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author Turgut Gurel,Student ID:2022400009
 * @since date:03.05.2024
 */

public class TurgutGurel {
    public static void main(String[] args) throws FileNotFoundException {

        //chosenMethod equals 1 for Brute force
        //chosenMethod equals 2 for Ant Colony Optimization
        int chosenMethod=2;

        //Boolean pheromonePath equals true for pheromone path
        //Boolean pheromonePath equals false for shortest distance path
        Boolean pheromonePath=true;

        //File handleing
        File file= new File("input01.txt");
        Scanner scanner=new Scanner(file);

        ArrayList<ArrayList<Double>> cityCoordinates=new ArrayList<>();

        //reading input files
        while (scanner.hasNextLine()){
            String[] a=scanner.nextLine().split(",");
            ArrayList<Double> k=new ArrayList<>();
            k.add(Double.parseDouble(a[0]));
            k.add(Double.parseDouble(a[1]));
            cityCoordinates.add(k);
        }

        //Getting all paths and distances to each city from other
        ArrayList<ArrayList<Double>> allDists=new ArrayList<>();
        for(ArrayList<Double> city_coords:cityCoordinates){
            ArrayList<Double> node_distances=new ArrayList<>();
            for(ArrayList<Double> secondcity:cityCoordinates){
                double x_dist=Math.pow(city_coords.get(0)-secondcity.get(0),2);
                double y_dist=Math.pow(city_coords.get(1)-secondcity.get(1),2);
                double totalDist=Math.sqrt(x_dist+y_dist);
                if(totalDist==0){
                    node_distances.add(0.0);
                }
                else node_distances.add(totalDist);//each city distances one by one
            }
            allDists.add(node_distances);//all distances added to a ArrayList
        }

        //Initializing the array which is going to permutated
        int[] permutationArray=new int[cityCoordinates.size()];

        //Adding each city index to a list to permumate it
        //permutate handles all permutations
        for(int cityIndex=0;cityIndex<permutationArray.length;cityIndex++){
            permutationArray[cityIndex]=cityIndex;
        }
        //Adding minimum distance to a Array to keep it at heap
        //to avoid complications
        double[] minimumDist=new double[1];
        minimumDist[0]=Double.MAX_VALUE;
        //bestRoute array to catch best route out of all permutations
        ArrayList<ArrayList<Double>> bestRoute=new ArrayList<>();

        //If blocks to handle choosen methods(ant colony optimization or brute force)
        if(chosenMethod==1) {

            //If there is too many cities in a file it takes too long to calculate it with brute force
            //so exit
            if(cityCoordinates.size()>25){
                System.out.println("It takes too long to calculate");
                System.exit(1);
            }

            //Handleing the time it takes to calculate shortest path with brute force method
            double first=System.currentTimeMillis();
            //Calling brute force method for specific cities in file
            ArrayList<Double> a= brute_force(permutationArray, 1, minimumDist, allDists, bestRoute);
            double last=System.currentTimeMillis();
            //Printing choosen method and shortest distance
            System.out.println("Choosen method: Brute Force");
            //Brute force method returns shortest distance as the last index of ArrayList
            System.out.printf("Shortest distance: %.5f", a.getLast());
            System.out.println();
            //Removing a from ArrayList
            a.removeLast();
            ArrayList<Integer> intArray=new ArrayList<>();
            //As I used indexes changing them from indexes to numbers
            //starting from 1
            for(int k=0;k<a.size();k++){
                intArray.add(a.get(k).intValue()+1);
            }
            //Printing shortest path and time it takes to calculate it
            System.out.println("Shortest path: " + intArray);
            System.out.printf("Time it takes to find shortest path: %.2f seconds" , (last-first)/1000);

            //Drawing nodes and pathss to visualize the shortest path
            StdDraw.setCanvasSize(900,900); // Set canvas size for visualization
            StdDraw.setXscale(0,1); // Set X scale for visualization
            StdDraw.setYscale(0,1); // Set Y scale for visualization
            StdDraw.enableDoubleBuffering();
            //Drawing path
            for(int node=0;node< intArray.size()-1;node++){
                double x1=cityCoordinates.get(intArray.get(node)-1).get(0);
                double y1=cityCoordinates.get(intArray.get(node)-1).get(1);
                double x2=cityCoordinates.get(intArray.get(node+1)-1).get(0);
                double y2=cityCoordinates.get(intArray.get(node+1)-1).get(1);
                StdDraw.line(x1,y1,x2,y2);
            }
            //number to handle cities indexes starting from 1
            int number=1;
            //Drawing cities as filled circles
            for (ArrayList<Double> cities:cityCoordinates){
                //if number equals 1 it is the starting point (Migros)
                if(number==1){
                    StdDraw.setPenColor(StdDraw.ORANGE);
                    StdDraw.filledCircle(cities.get(0),cities.get(1),0.02);
                    StdDraw.setPenColor(StdDraw.BLACK);
                    StdDraw.text(cities.get(0),cities.get(1),number+"");
                }

                else {
                    StdDraw.setPenColor(StdDraw.GRAY);
                    StdDraw.filledCircle(cities.get(0), cities.get(1), 0.02);
                    StdDraw.setPenColor(StdDraw.BLACK);
                    StdDraw.text(cities.get(0), cities.get(1), number + "");
                }
                //increment number
                number++;

            }
            StdDraw.show();
        }
        //Handleing ant colony optimization
        else if (chosenMethod==2) {
            antColonyOptim(cityCoordinates,allDists,pheromonePath);

        }

    }
    //Brute force method(all permutations)

    /**
     *
     * @param cities Cities which get permutated
     * @param k The city permutation starts with
     * @param minDistance Minimum distance till that permutation
     * @param allDists The ArrayList of ArrayList that stores all distances between cities
     * @param best_route The shortest routes stored in this arrayList
     * @return A ArrayList with shortest path and shortest distance combined
     */
    private static ArrayList<Double> brute_force(int[] cities, int k, double[] minDistance, ArrayList<ArrayList<Double>> allDists, ArrayList<ArrayList<Double>> best_route) {
        //Main if to stop recursive call
        if (k == cities.length) {
            double distance=0;
            //Calculate distance if recursive call is finished
            for (int j=0;j<cities.length-1;j++){
                distance+= allDists.get(cities[j]).get(cities[j+1]);
            }
            //adding last distance to permutation
            distance+=allDists.get(cities[cities.length-1]).get(cities[0]);
            //if distance smaller than min distance change it to new one
            if (distance < minDistance[0]) {
                //adding best paths to bestPaths ArrayList
                ArrayList<ArrayList<Double>> bestPaths=new ArrayList<>();
                //Initializing first ArrayList in bestPaths
                ArrayList<Double> capsuleArray=new ArrayList<>();
                bestPaths.add(capsuleArray);
                //Cities is permutated array
                for (int i:cities){
                    bestPaths.get(0).add((double)i);
                }
                //Adding first city back to array to make it a circle
                bestPaths.get(0).add(bestPaths.getFirst().getFirst());
                //Changing minimum distance
                minDistance[0]=distance;
                //Adding minimum distance known at this iteration to the paths last index
                bestPaths.get(0).add(minDistance[0]);
                //Copying the array
                best_route.addAll(bestPaths);
            }
        }
        //Recursive permutation
        else {
            for (int i = k; i < cities.length; i++) {
                //temp is inital city handled out of permutations and added at last
                int temp = cities[i];
                cities[i] = cities[k];
                cities[k] = temp;
                //recursive call
                brute_force(cities, k + 1,minDistance,allDists,best_route);
                temp = cities[k];
                cities[k] = cities[i];
                cities[i] = temp;
            }
        }
        //Returning best path when all the recursion is finished
        return best_route.getLast();
    }

    // Ant colony optimization algorithm

    /**
     *
     * @param cityCoordinates The 2D ArrayList stores all cities coordinates
     * @param allDists The ArrayList of ArrayList that stores each cities distance with others
     * @param pheromonepather The boolean that handles if user wants to draw a pheromonepath or not
     * @return the shortest distance
     */
    public static ArrayList<Integer> antColonyOptim(ArrayList<ArrayList<Double>> cityCoordinates,ArrayList<ArrayList<Double>> allDists,Boolean pheromonepather){
        //Handling starting time
        long firsttime=System.currentTimeMillis();
        //Handling all constants
        int iterationNumber=100; //Iteration count
        int antCount=50; //Ant count
        double degregationFactor=0.9;
        //Optimal alpha and beta values
        final double alpha=0.97;
        final double beta=2.8;
        double initalPheromone=0.1;
        final double Qconstant=0.0001;
        //minimum distance set to a huge number to change it with smaller ones
        double min_dist=10000000;
        //Handleing shortest path
        ArrayList<Integer> shortest_path=new ArrayList<>();

        //setting each nodes pheromones
        double[][] node_pheromone=new double[cityCoordinates.size()][cityCoordinates.size()];
        for(double[] i:node_pheromone){
            for(int j=0;j<i.length;j++){
                i[j]=initalPheromone;
            }
        }

        //Each iteration have number of ants(ant count)
        for(int iterationCount=0;iterationCount<iterationNumber;iterationCount++){
            for(int i=0;i<antCount;i++){
                //Handling each nodes probabilty of getting visited by a ant
                double[][] probabilityArray=new double[cityCoordinates.size()][cityCoordinates.size()];

                //Setting probabilities of each node at starting of each ant
                for(int k=0;k<probabilityArray.length;k++){
                    for(int l=0;l<probabilityArray[k].length;l++) {
                        if (allDists.get(k).get(l)!=0) {
                            probabilityArray[k][l] = Math.pow(node_pheromone[k][l], alpha) / Math.pow(allDists.get(k).get(l), beta);
                        }
                        //if distance equals 0 to handle infinite probability adding -1 to Array
                        else {
                            probabilityArray[k][l]=-1;
                        }
                    }
                }

                //visitedNodes to prevent from visiting it again
                ArrayList<Integer> visitedNodes=new ArrayList<>();
                //Choosing a random city out of cities for ant to start with
                int startingNode=(int)(Math.random()*(cityCoordinates.size()));
                //starting total dist with 0 for each ant
                double total_dist=0;

                //while loop for each ant to traverse all nodes
                while(true) {
                    if (visitedNodes.size()==cityCoordinates.size()){
                        break;
                    }
                        //Adding the node ant traverses to visitedNodes
                        visitedNodes.add(startingNode);
                        //initializing probabilities for ants
                        for(int k=0;k<probabilityArray.length;k++){
                            for(int l=0;l<probabilityArray[k].length;l++) {
                                if(visitedNodes.contains(l)){
                                    probabilityArray[k][l]=-1;
                                }
                            }
                        }

                        //Initializing probability matrix for cities to chose
                        ArrayList<Double> probability_matrix=new ArrayList<>();

                            for (int elem = 0; elem < probabilityArray.length; elem++) {
                                double sumOfProbabilities = 0;
                                for (int k = 0; k < probabilityArray[startingNode].length; k++) {
                                        if(probabilityArray[startingNode][k]!=-1) {
                                            sumOfProbabilities += probabilityArray[startingNode][k];
                                        }
                                }
                                double eachProbability = probabilityArray[startingNode][elem] / sumOfProbabilities;
                                if (eachProbability>0) {
                                    probability_matrix.add(eachProbability);
                                }
                                else {
                                    probability_matrix.add(0.0);
                                }

                            }



                        //find the new node
                        double newNodeValue=Math.random();
                        //for each probability in probability matrix
                        //sum of probabilities
                        double sum=0;
                        //Handling city index
                        int index=0;
                        //Handling which city ant goes next
                        for(double k:probability_matrix){
                                if (newNodeValue < sum + k) {
                                    total_dist += allDists.get(startingNode).get(index);
                                    startingNode = index;
                                    break;
                                }
                                //increment i and add sum with k
                                else {
                                    index++;
                                    sum+=k;
                                }

                            }
                        }
                //Adding first city back to visitedNodes to traverse it again
                visitedNodes.add(visitedNodes.get(0));


                //Adding traversed paths pheromone by the Delta value
                for(int pheromonepath=0;pheromonepath<visitedNodes.size()-1;pheromonepath++){
                    node_pheromone[visitedNodes.get(pheromonepath)][visitedNodes.get(pheromonepath+1)]+=Qconstant/total_dist;
                    node_pheromone[visitedNodes.get(pheromonepath+1)][visitedNodes.get(pheromonepath)]+=Qconstant/total_dist;
                }
                //Handling the shortest path out of all
                if(total_dist+allDists.get(visitedNodes.get(0)).get(visitedNodes.get(visitedNodes.size()-2))<min_dist){
                    min_dist=total_dist+allDists.get(visitedNodes.getFirst()).get(visitedNodes.get(visitedNodes.size()-2));
                    shortest_path=visitedNodes;
                }

            }

            //evaporation of pheromone at each iteration
            for(int i=0;i< node_pheromone.length;i++){
                for(int k=0 ; k<node_pheromone[i].length;k++){
                    node_pheromone[k][i]=node_pheromone[k][i]*degregationFactor;
                }
            }
        }

        //Setting indexes to numbers starting from 1
        for(int i=0;i<shortest_path.size();i++){
            shortest_path.set(i,shortest_path.get(i)+1);
        }

        ArrayList<Integer> minimumDist=new ArrayList<>();
        //Calculating the minimum distance out of shortest path
        for(int k=0;k<shortest_path.size();k++){
            if (shortest_path.get(k)==1){
                for(int j=k;j<shortest_path.size()-1;j++){
                    minimumDist.add(shortest_path.get(j));
                }
                for (int h=0;h<shortest_path.size();h++){
                    minimumDist.add(shortest_path.get(h));
                    if (shortest_path.get(h)==1){
                        break;
                    }
                }
            }
        }

        //Printing out chosen method,
        //shortest distance,shortest path and time
        System.out.println("Choosen Method: Ant colony optimization");
        System.out.printf("Shortest distance: %.5f", min_dist);
        System.out.println();
        System.out.println("Shortest path: "+ minimumDist);
        System.out.printf("Time it takes to find shortest path : %.2f seconds",((System.currentTimeMillis()-firsttime))/1000.0);



        //Drawing the pheromone path or shortest path
        StdDraw.setCanvasSize(900,900); // Set canvas size for visualization
        StdDraw.setXscale(0,1); // Set X scale for visualization
        StdDraw.setYscale(0,1); // Set Y scale for visualization
        StdDraw.enableDoubleBuffering();

        //Handling if pheromone path or shortest path
        if (pheromonepather==false){
            //Drawing cities using StdDraw
            for(int i=0;i<minimumDist.size()-1;i++){
                double x1=cityCoordinates.get(minimumDist.get(i)-1).get(0);
                double y1=cityCoordinates.get(minimumDist.get(i)-1).get(1);
                double x2=cityCoordinates.get(minimumDist.get(i+1)-1).get(0);
                double y2=cityCoordinates.get(minimumDist.get(i+1)-1).get(1);
                StdDraw.line(x1,y1,x2,y2);
            }
            int number=1;
            for(ArrayList<Double> cities:cityCoordinates){
                if(number==1){
                    StdDraw.setPenColor(StdDraw.ORANGE);
                    StdDraw.filledCircle(cities.get(0),cities.get(1),0.02);
                    StdDraw.setPenColor(StdDraw.BLACK);
                    StdDraw.text(cities.get(0),cities.get(1),number+"");
                }
                else {
                    StdDraw.setPenColor(StdDraw.GRAY);
                    StdDraw.filledCircle(cities.get(0), cities.get(1), 0.02);
                    StdDraw.setPenColor(StdDraw.BLACK);
                    StdDraw.text(cities.get(0), cities.get(1), number + "");
                }
                number++;
            }
            StdDraw.show();
        }
        //Handling if pheromone path or shortest path
        if (pheromonepather==true){

            for(int firstCity=0;firstCity<cityCoordinates.size();firstCity++){
                for(int secondCity=firstCity;secondCity<cityCoordinates.size();secondCity++){
                    double x1=cityCoordinates.get(firstCity).get(0);
                    double y1=cityCoordinates.get(firstCity).get(1);
                    double x2=cityCoordinates.get(secondCity).get(0);
                    double y2=cityCoordinates.get(secondCity).get(1);

                    StdDraw.setPenRadius(node_pheromone[firstCity][secondCity]);
                    StdDraw.line(x1,y1,x2,y2);
                }
            }
            int number=1;
            for(ArrayList<Double> cities:cityCoordinates){


                    StdDraw.setPenColor(StdDraw.GRAY);
                    StdDraw.filledCircle(cities.get(0), cities.get(1), 0.02);
                    StdDraw.setPenColor(StdDraw.BLACK);
                    StdDraw.text(cities.get(0), cities.get(1), number + "");
                number++;
            }
            StdDraw.show();
        }
        return minimumDist;
        }
    }
