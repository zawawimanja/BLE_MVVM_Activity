package no.nordicsemi.android.bluetooth.data.local.entity;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


@Entity(tableName = "word_table")
public class BleEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo(name = "word")
    private String mWord;
    private String mReceive;



    public BleEntity(@NonNull String word) {
        this.mWord = word;

    }


    /*
     * This constructor is annotated using @Ignore, because Room expects only
     * one constructor by default in an entity class.
     */

    @Ignore
    public BleEntity(int id, @NonNull String word) {
        this.id = id;
        this.mWord = word;
    }

    public void setReceive(String receive){
        this.mReceive= receive;
    }
    public void setWord(String word){
        this.mWord= word;
    }

    public String getReceive(){
        return  this.mReceive;
    }

    public String getWord() {
        return this.mWord;
    }

    public int getId() {return id;}

    public void setId(int id) {
        this.id = id;
    }
}
