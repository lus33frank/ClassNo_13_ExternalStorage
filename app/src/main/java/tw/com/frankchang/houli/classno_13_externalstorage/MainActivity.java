package tw.com.frankchang.houli.classno_13_externalstorage;

import android.content.DialogInterface;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Spinner spinner;
    RadioButton rbPublic, rbPrivate;
    TextView tvShow, tvPath;
    EditText etInput, etFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //findViewById
        findviewer();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //設定下拉選單
        setSpinner();
    }

    //findViewById
    private void findviewer(){
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(SpinnerListener);

        rbPublic = (RadioButton) findViewById(R.id.radioButton);
        rbPublic.setOnCheckedChangeListener(radioButtonListener);

        rbPrivate = (RadioButton) findViewById(R.id.radioButton2);
        rbPrivate.setOnCheckedChangeListener(radioButtonListener);

        tvShow = (TextView) findViewById(R.id.textView);
        tvPath = (TextView) findViewById(R.id.textView2);
        etInput = (EditText) findViewById(R.id.editText);
    }

    //檢查SD Card狀態
    protected boolean isSDCardExist(){
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)){
            return true;
        }

        return false;
    }

    //設定下拉選單
    private void setSpinner(){
        String[] data = getPath().list();
        ArrayAdapter<String> adt = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adt);
    }

    //Spinner監聽器
    private AdapterView.OnItemSelectedListener SpinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            showData();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            //沒用到
        }
    };

    //讀取檔案內容
    private void showData(){
        File path = getPath();
        File file = new File(path, getSpinnerItemSelectedName());

        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            tvShow.setText(new String(buffer));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getSpinnerItemSelectedName(){
        String filename = spinner.getSelectedItem().toString();

        return filename;
    }

    //radioButt監聽器
    CompoundButton.OnCheckedChangeListener radioButtonListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            setSpinner();
        }
    };

    //儲存按鈕監聽器
    public void saveOnClick(View v){
        //當選擇用公開區時必需檢查SD Card狀態是否存在，不存在則離開
        if (!isSDCardExist() && rbPublic.isChecked()){
            Toast.makeText(MainActivity.this, R.string.noSDCard, Toast.LENGTH_SHORT).show();

            return;
        }

        //AlertDialog
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_view, null);
        //建立findViewById
        etFileName = (EditText) dialogView.findViewById(R.id.editText2);

        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_save_title)
                .setView(dialogView)
                .setPositiveButton(R.string.dialog_btn_ok, save_DialogListener)
                .setNegativeButton(R.string.dialog_btn_cancel, save_DialogListener)
                .show();
    }

    //save_Dialog監聽器
    private DialogInterface.OnClickListener save_DialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    save_File();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    Toast.makeText(MainActivity.this, R.string.save_cancel, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    //取得儲存檔案路徑
    private File getPath(){
        File path = null;

        if (rbPublic.isChecked()){
            //公有區
            path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        }
        else if (rbPrivate.isChecked()){
            //私有區
            path = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        }

        return path;
    }

    //儲存寫入檔案
    private void save_File(){
        boolean save_ok = false;

        //建立file物件
        File path = getPath();
        File file = new File(path, etFileName.getText().toString());

        //寫入
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write((etInput.getText().toString() + "\n").getBytes());
            fos.close();
            save_ok = true;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //回饋資訊
        if (save_ok){
            tvPath.setText(path.toString());
            setSpinner();
            Toast.makeText(this, R.string.save_ok, Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, R.string.save_error, Toast.LENGTH_SHORT).show();
        }

    }

    //附加按鈕監聽器
    public void appendOnClick(View v){
        boolean save_ok = false;

        //建立file物件
        File path = getPath();
        File file = new File(path, getSpinnerItemSelectedName());

        //寫入
        try {
            FileOutputStream fos = new FileOutputStream(file, true);
            fos.write((etInput.getText().toString() + "\n").getBytes());
            fos.close();
            save_ok = true;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //回饋資訊
        if (save_ok){
            showData();
            Toast.makeText(this, R.string.append_ok, Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, R.string.append_error, Toast.LENGTH_SHORT).show();
        }
    }

    //刪除按鈕監聽器
    public void deleteOnClick(View v){
        //AlertDialog的訊息內容
        String strMessage = getResources().getString(R.string.dialog_delete_Msg_star);
        strMessage += getSpinnerItemSelectedName();
        strMessage += getResources().getString(R.string.dialog_delete_Msg_end);

        //AlertDialog
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_delete_title)
                .setMessage(strMessage)
                .setPositiveButton(R.string.dialog_btn_ok, delete_DialogListener)
                .setNegativeButton(R.string.dialog_btn_cancel, delete_DialogListener)
                .show();
    }

    //delete_Dialog監聽器
    DialogInterface.OnClickListener delete_DialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    //建立file物件
                    File path = getPath();
                    File file = new File(path, getSpinnerItemSelectedName());
                    //刪除動作
                    if (file.delete()){
                        setSpinner();
                        Toast.makeText(MainActivity.this, R.string.delete_ok, Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(MainActivity.this, R.string.delete_error, Toast.LENGTH_SHORT).show();
                    }
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //取消刪除
                    Toast.makeText(MainActivity.this, R.string.delete_cancel, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}
