package id.ac.petra.informatika.amuze.android;

/**
 * Created by Private on 11/4/2015.
 */
public class QRItem {
    int _row, _col, _size;
    String _qrCode;
    QRItem(int row, int col, int size, String qrCode){
        _row = row;
        _col = col;
        _size = size;
        _qrCode = qrCode;
    }
    void set(int row, int col, int size, String qrCode){
        _row = row;
        _col = col;
        _size = size;
        _qrCode = qrCode;
    }
    int row(){return _row;}
    int col(){return _col;}
    int size(){return _size;}
    String qrCode(){return _qrCode;}
}
