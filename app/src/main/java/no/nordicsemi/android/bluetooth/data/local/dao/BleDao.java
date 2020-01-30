package no.nordicsemi.android.bluetooth.data.local.dao;



import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import no.nordicsemi.android.bluetooth.data.local.entity.BleEntity;

@Dao
public interface BleDao {


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(BleEntity BleEntity);

    @Query("DELETE FROM word_table")
    void deleteAll();

    @Delete
    void deleteBleEntity(BleEntity BleEntity);

    @Query("SELECT * from word_table LIMIT 1")
    BleEntity[] getAnyBleEntity();

    @Query("SELECT * from word_table ORDER BY word ASC")
    LiveData<List<BleEntity>> getAllBleEntitys();

    // asynchronously via observable queries
    //Flowable<List<User>> getUsers();

    @Query("SELECT * FROM word_table where word=:number")
    public BleEntity getNumber(String number);

    @Update
    void update(BleEntity... BleEntity);
}