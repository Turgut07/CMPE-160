import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.Integer.parseInt;

/**
 * @author Turgut Gurel,student id:2022400009
 * @since Date:26.03.2024
 */
public class TurgutGurel {
    public static void main(String[] args) throws FileNotFoundException {

        //File handleing
        File file1=new File("city_connections.txt");
        File file2 = new File("city_coordinates.txt");
        Scanner connectionsHandle=new Scanner(file1);
        Scanner coordinatesHandle=new Scanner(file2);
        //ArrayList to hold city objects
        ArrayList<City> cityAdresses=new ArrayList<>();
        //Handling city_coordinates.txt and converting them to city objects
        while(coordinatesHandle.hasNextLine()){
            String[] scannerParse=coordinatesHandle.nextLine().split(", ");
            City city=new City(scannerParse[0],parseInt(scannerParse[1]),parseInt(scannerParse[2]));
            cityAdresses.add(city);
        }

        //Handling city_connections.txt and adding each city the Arraylist of connecting cities
        while (connectionsHandle.hasNextLine()) {
            String[] connection = connectionsHandle.nextLine().split(",");
            int index1 = findIndex(connection[0], cityAdresses);
            int index2 = findIndex(connection[1], cityAdresses);

            cityAdresses.get(index1).cityConnections.add(connection[1]);
            cityAdresses.get(index2).cityConnections.add(connection[0]);
        }

        //Taking inputs from user
        Scanner input =new Scanner(System.in);
        String startingCity="";
        String finishingCity="";
        //if both starting city and finishing city is not empty and true called city names input
        //taking overs
        while (startingCity.length()==0 ||finishingCity.length()==0) {
            //if startingCity is not empty stop taking input for startingCity
            if (startingCity.length()==0) {

                System.out.println("Enter starting city: ");
                startingCity = input.nextLine();

                if (findIndex(startingCity , cityAdresses) == -1) {
                    System.out.println("City named " + startingCity + " not found. Please enter a valid city name.");
                    startingCity = "";
                }
            }
            //if finishing city is not empty stop taking input for finishingCity
            else if (finishingCity.length()==0) {
                System.out.println("Enter destination city: ");
                finishingCity= input.nextLine();
                //findIndex method returns -1 if no city can be found
                //To handle if the input is not a city name or written wrong
                if (findIndex(finishingCity , cityAdresses) == -1) {
                    System.out.println("City named " +  finishingCity+" not found. Please enter a valid city name.");
                    finishingCity= "";
                }
            }
        }

        //the 2D matrix whose each element is the array that holds {index of city,the shortest path to that city,the index of parent city}
        ArrayList<double[]> matrix= new ArrayList<double[]>();
        for (int i = 0; i < cityAdresses.size(); i++) {
            //Array list contains city indexes city path lengths and city before
            double[] city_data_int=new double[3];
            city_data_int[0]=(findIndex(cityAdresses.get(i).cityName,cityAdresses));
            //Setting the startingCity distance as 0
            if(city_data_int[0]==findIndex(cityAdresses.get(findIndex(startingCity,cityAdresses)).cityName,cityAdresses)){
                city_data_int[1]=0;
            }
            //Setting the first index of all cities as infinite other than startingCity
            else {
            city_data_int[1]=(Integer.MAX_VALUE);
            }
            //Setting the index of parent city a number out of 0 to 80
            city_data_int[2]=(10000);
            matrix.add(city_data_int);
        }
        ArrayList<String> path=new ArrayList();


        //Handling if no path could be found by the if block
        //Printing out the Total Distance and Path
        //Dijkstra method returns us the path and the distance in one Arraylist
        //this ArrayList contains distance at last element and the path at other elements
        if (!Dijkstra(matrix,findIndex(startingCity,cityAdresses),findIndex(finishingCity,cityAdresses),cityAdresses).isEmpty()){
            path=Dijkstra(matrix,findIndex(startingCity,cityAdresses),findIndex(finishingCity,cityAdresses),cityAdresses);
            System.out.println();
            System.out.print("Total Distance: ");
            System.out.printf("%.2f. ",Double.parseDouble(path.get(path.size()-1)));
            System.out.println();
            System.out.print("Path:");
            for (int i=0;i<path.size()-2;i+=1){
                System.out.print(" "+path.get(i)+"->");
            }
            System.out.print(path.get(path.size()-2));

        }
        else System.out.println("No path could be found");

        //Again if block makes sure that there is a path we drawing
        if(!Dijkstra(matrix,findIndex(startingCity,cityAdresses),findIndex(finishingCity,cityAdresses),cityAdresses).isEmpty()){
            //Drawing the map and the roads
            int width = 2377;
            int height = 1055;
            StdDraw.setCanvasSize(width / 2, height / 2);
            StdDraw.setXscale(0, width);
            StdDraw.setYscale(0, height);
            StdDraw.picture(width / 2.0, height / 2.0, "map.png", width, height);
            StdDraw.enableDoubleBuffering();
            StdDraw.setPenColor(StdDraw.GRAY);
            //Getting cities from cityAdresses list and printing their names on map at x,y coordinates given
            for (City city : cityAdresses) {
                StdDraw.text(city.x, city.y + 20, city.cityName);
                StdDraw.filledCircle(city.x, city.y, 4);
                //printing lines between connected cities
                for (String connection : city.cityConnections) {
                    int connnecitonIndex = findIndex(connection, cityAdresses);
                    StdDraw.line(city.x, city.y, cityAdresses.get(connnecitonIndex).x, cityAdresses.get(connnecitonIndex).y);
                }
            }
            //Drawing the path we found
            StdDraw.setPenColor(StdDraw.CYAN);
            StdDraw.setPenRadius(0.005);

            for (int i = 0; i < path.size()-1; i++) {
                //take locations from cityAdresses ArrayList
                int cityLocation = findIndex(path.get(i),cityAdresses);
                //Not taking last two indexes that the last index is Total Distance and the before last
                //index is the last city that handled by the city before that
                if (i != path.size() - 2) {
                    int city2Index = findIndex(path.get(i + 1),cityAdresses);
                    StdDraw.line(cityAdresses.get(cityLocation).x, cityAdresses.get(cityLocation).y, cityAdresses.get(city2Index).x, cityAdresses.get(city2Index).y);
                }
                //Printing the path cities in blue
                StdDraw.text(cityAdresses.get(cityLocation).x, cityAdresses.get(cityLocation).y + 20, cityAdresses.get(cityLocation).cityName);
                StdDraw.filledCircle(cityAdresses.get(cityLocation).x, cityAdresses.get(cityLocation).y, 5);
            }
            StdDraw.show();
        }
    }

    /**
     *
     * @param city1name:First city
     * @param city2name:Second city
     * @param cityAdresses: The list that stores city objects
     * @return : the distances between two cities
     */
    public static double findDistance(String city1name, String city2name, ArrayList<City> cityAdresses) {
        City city1 = cityAdresses.get(findIndex(city1name, cityAdresses));
        City city2 = cityAdresses.get(findIndex(city2name, cityAdresses));
        int x1 = city1.x;
        int x2 = city2.x;
        int y1 = city1.y;
        int y2 = city2.y;
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }


    /**
     *
     * @param cityNameStr:The string of the city name
     * @param cityAdresses : The list that stores city objects
     * @return : The index of City object
     */
    //Find index method returns the Index of a string CityName as a
    public static int findIndex(String cityNameStr, ArrayList<City> cityAdresses) {
        int i = 0;
        for (City city : cityAdresses) {
            if (city.cityName.equals(cityNameStr)) {
                return i;

            }
            i += 1;
        }
        return -1;
    }
    //the shortest path algorithm that used
    //the algorithm handles all cities as integer indexes of them in cityAdresses arraylist to make things easier
    //we impelemnt the findIndex and findDistance algorithms to make things easier

    /**
     *
     * @param matrix: the 2D matrix whose each element is the array that holds {index of city,the shortest path to that city,the index of parent city}
     * @param startingCity: Source city
     * @param finishingCity: Destination city
     * @param cityAdresses: The list that holds city objects
     * @return: ArrayList that holds the path and the distance
     */

    public static ArrayList<String> Dijkstra(ArrayList<double[]> matrix, int startingCity, int finishingCity, ArrayList<City> cityAdresses) {
        int cityHandle=startingCity;
        //Creating visitedCities ArrayList to dont trace back to parent city
        ArrayList<Integer> visitedCities=new ArrayList<>();
        //while loop to main loop that implements dijkstra's algorithm
        while (cityHandle!=finishingCity) {

            //This method returns a empty list if no path could be found
            if (visitedCities.contains(cityHandle)) {
                ArrayList<String> no=new ArrayList<>();
                return no;
            }
            //Adding the city we are checking right now to visitedCities to prevent tracing back
            visitedCities.add(cityHandle);
            //Intiliazing prevDist as 0 for startingCity
            double prevDist = 0;

            //Changing the previous distance to the previous distance of the city we are handling
            for (double[] i : matrix) {
                if (i[0] == cityHandle) {
                    prevDist = i[1];
                }
            }
            //This is the main part of algorithm
            for (String i : cityAdresses.get(cityHandle).cityConnections) {
                for (double[] j : matrix) {
                    if (j[0] == findIndex(i, cityAdresses)) {
                        //If the previous distance from Starting city + the distance to the one of the connecting cities
                        //We then change the distance of that city to previous distance+ the other distance we just calculated
                        if (prevDist + findDistance(i, cityAdresses.get(cityHandle).cityName, cityAdresses) < j[1]) {
                            j[2] = cityHandle;
                            j[1] = prevDist + findDistance(i, cityAdresses.get(cityHandle).cityName, cityAdresses);
                        }
                    }
                }
            }
            //Adding visitedCities the city we are handling now to not deal with it again
            visitedCities.add(cityHandle);
            ArrayList<Double> compareArray = new ArrayList<>();
            //Add compareArray k[1] the distances of all cities except the cities we visited
            //Compare them later
            for (double[] k : matrix) {
                if (!visitedCities.contains((int) k[0])) {
                    compareArray.add(k[1]);
                }
            }
            //Comparing the distances of cities we added to get there
            //finding the minimum of the compareArray
            double min = 1000000;
            for (double k : compareArray) {
                if (k < min) {
                    min = k;
                }
            }

            //Finding the shortest path city to that moment
            //from matrix and changing the city we are checking the connections of

            for (double[] z : matrix) {
                if (z[1] == min) {
                    cityHandle = (int) z[0];
                }
            }
            //End of while loop
        }
        //To find the path we are using parent cities the third index of our elements of matrix
        //otherCity is an index we are using here
        int otherCity=cityHandle;
        ArrayList<String> path=new ArrayList<>();
        //We are coming back from the parent of finishing city to starting city and that gives the path
        while (true) {
            if (otherCity == startingCity) {
                path.add(cityAdresses.get((int)cityHandle).cityName);
                break;
            }

            for (double[] k : matrix) {
                if (k[0] == otherCity) {
                    otherCity=(int)k[2];
                    path.addFirst(cityAdresses.get((int)otherCity).cityName);
                    break;
                }
            }

        }
        //Adding path distance as String
        //Handle it later
        path.add(matrix.get(cityHandle)[1]+"");
        return path;
    }
}
