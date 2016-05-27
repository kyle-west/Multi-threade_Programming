package cs246.kyle.west.multi_threadedprogramming;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ArrayAdapter<String> _content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void create_onClick(View v) {
        new Write(this).execute(findViewById(R.id.progressBar));
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void load_onClick(View v) {
        new Load(this).execute(findViewById(R.id.progressBar),findViewById(R.id.listOut));
    }

    public void clear_onClick(View v) {
        ListView listOut = (ListView) findViewById(R.id.listOut);
        _content = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1);
        listOut.setAdapter(_content);

        Toast.makeText(this, "Removed list", Toast.LENGTH_SHORT).show();
    }
}

class Write extends AsyncTask<View, Integer, Void>{

    private Context _context;
    private ProgressBar prog;

    public Write(Context con) {
        _context = con;
    }

    @Override
    protected Void doInBackground(View... params) {
        prog = (ProgressBar) params[0];
        FileOutputStream outputStream;
        try {
            outputStream = _context.openFileOutput("numbers.txt", Context.MODE_PRIVATE);
            for (int i = 0; i < 11; i++) {
                String printStr = i + "\n";
                outputStream.write(printStr.getBytes());
                Thread.sleep(250);
                publishProgress(i*10);
            }
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onProgressUpdate(Integer... params) {
        prog.setProgress(params[0]);
    }

    protected void onPostExecute(Void params){
        File file = new File(_context.getFilesDir(), "numbers.txt");
        Toast.makeText(_context, "Successfully wrote: " + file, Toast.LENGTH_SHORT).show();
    }
}

class Load extends AsyncTask<View, Integer, String[]>{

    private Context _context;
    private ListView _listOut;
    private ProgressBar prog;

    public Load(Context con) {
        _context = con;
    }

    protected void onPreExecute(){
        Toast.makeText(_context, "Reading from \"numbers.txt\"", Toast.LENGTH_SHORT).show();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected String[] doInBackground(View... params) {
        prog = (ProgressBar) params[0];
        _listOut = (ListView) params[1];

        List<String> store = new ArrayList<>();
        File file = new File(_context.getFilesDir(), "numbers.txt");
        try(BufferedReader buffer = new BufferedReader(new FileReader(file))) {
            String line;
            int i = 1;
            while((line = buffer.readLine()) != null) {
                store.add(line);
                Thread.sleep(250);
                publishProgress(i*10);
                i++;
            }
            System.out.println("READ: " + file);
        }
        catch(Exception ex) {
            System.out.println("Error 1234");
            System.exit(-98);
        }

        return store.toArray(new String[store.size()]);
    }

    protected void onProgressUpdate(Integer... params) {
        prog.setProgress(params[0]);
    }

    protected void onPostExecute(String[] data){
        _listOut.setAdapter(new ArrayAdapter<>(
                _context,
                android.R.layout.simple_list_item_1,
                data));

        File file = new File(_context.getFilesDir(), "numbers.txt");
        Toast.makeText(_context, "Successfully read: " + file, Toast.LENGTH_SHORT).show();
    }
}
