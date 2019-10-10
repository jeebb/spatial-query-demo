package dev.duyng.spatialquerydemo;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class SpatialQueryDemoApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(SpatialQueryDemoApplication.class, args);
    }

    private static Map<String, Coordinate> locationMap = new HashMap<>();

    /**
     * Distances from Dublin to other places:
     * - Livermore: ~9.28 miles = ~14934.71 meters
     * - Oakland: ~19.62 miles = ~31575.33 meters
     * - San Francisco: ~26.91 miles = ~43307.45 meters
     */
    {
        // coordinate: x = longitude, y = latitude
        locationMap.put("Dublin", new Coordinate(-121.935791, 37.702152));
        locationMap.put("Oakland", new Coordinate(-122.271111, 37.804363));
        locationMap.put("San Francisco", new Coordinate(-122.446747, 37.733795));
        locationMap.put("Livermore", new Coordinate(-121.768005, 37.681873));
    }

    @Autowired
    private StoreLocationRepository repository;

    private GeometryFactory geoFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Override
    public void run(String... args) throws Exception {
        log.info("Experimenting geo query ...");
        initData();

        StoreLocation dublin = repository.findByStoreName("Dublin Store");
        // looking around Dublin for some stores

        List<StoreLocation> list1 = repository.findStoresAround(getLocationText(dublin.getLocationCoordinates()), 15000);
        log.info("Found {} store(s) with in {} meters", list1.size(), 15000);
        printOut(list1);

        List<StoreLocation> list2 = repository.findStoresAround(getLocationText(dublin.getLocationCoordinates()), 35000);
        log.info("Found {} store(s) with in {} meters", list2.size(), 35000);
        printOut(list2);

        List<StoreLocation> list3 = repository.findStoresAround(getLocationText(dublin.getLocationCoordinates()), 50000);
        log.info("Found {} store(s) with in {} meters", list3.size(), 50000);
        printOut(list3);
    }

    private void printOut(List<StoreLocation> storeLocations) {
        log.info("###");

        storeLocations.forEach(storeLocation -> {
            log.info("{} ({}, {})",
                storeLocation.getStoreName(), storeLocation.getLocationCoordinates().getX(), storeLocation.getLocationCoordinates().getY());
        });

        log.info("###");
    }

    private String getLocationText(Point location) {
        return String.format("POINT(%s %s)", location.getX(), location.getY());
    }

    private void initData() {
        if (repository.count() == 0) {
            log.info("Going to init some test data ...");

            for (String city : locationMap.keySet()) {
                StoreLocation storeLocation = new StoreLocation();
                storeLocation.setStoreName(city + " Store");
                storeLocation.setLocationCoordinates(geoFactory.createPoint(locationMap.get(city)));

                repository.save(storeLocation);
            }
        }

        log.info("Test data is ready for testing");
    }
}
