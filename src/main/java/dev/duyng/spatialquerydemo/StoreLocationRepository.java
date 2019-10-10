package dev.duyng.spatialquerydemo;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface StoreLocationRepository extends CrudRepository<StoreLocation, Long> {

    StoreLocation findByStoreName(String storeName);

    /*
    the stored geometry point with SRID 4326 is degree units.
    '<point>::geography' will help convert that point to a geography points with meters unit for distances

    some raw queries to test in DB client:
        select ST_Distance(store1.location_coordinates::geography, store2.location_coordinates::geography)
        from store_location store1, store_location store2 where store1.store_name = 'Dublin Store' and store2.store_name = 'San Francisco Store';

        select ST_Distance(store1.location_coordinates::geography, store2.location_coordinates::geography)
        from store_location store1, store_location store2 where store1.store_name = 'Dublin Store' and store2.store_name = 'Oakland Store';

        select ST_Distance(store1.location_coordinates::geography, store2.location_coordinates::geography)
        from store_location store1, store_location store2 where store1.store_name = 'Dublin Store' and store2.store_name = 'Livermore Store';
     */
    @Query(
        value = "select * from store_location s "
            + "where equals(ST_GeometryFromText(:locationText, 4326), s.location_coordinates) = false "
            + "and ST_Distance(s.location_coordinates\\:\\:geography, ST_GeometryFromText(:locationText, 4326)\\:\\:geography) <= :radiusInMeters",
        nativeQuery = true
    )
    List<StoreLocation> findStoresAround(
        @Param("locationText") String location,
        @Param("radiusInMeters") double radiusInMeters
    );
}
