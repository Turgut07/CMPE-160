import java.util.ArrayList;


    public class City {
        public String cityName;

        public int x;

        public int y;

        public ArrayList<String> cityConnections;

        /**
         *
         * @param cityName:keep city's name
         * @param x : the x coordinate of the city object
         * @param y: the y coordinate of the city object
         */
        public City(String cityName, int x, int y){

            this.cityName = cityName;
            this.x = x;
            this.y = y;
            cityConnections = new ArrayList<>();

        }

    }

