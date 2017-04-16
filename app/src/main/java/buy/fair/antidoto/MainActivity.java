package buy.fair.antidoto;

import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText textSearchString;
    private ListView resultadosLV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonScan = (Button) findViewById(R.id.buttonScan);
        buttonScan.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                startScan();
            }
        });

        Button buttonSearch = (Button) findViewById(R.id.buttonSearch);
        buttonSearch.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                searchString();
            }
        });

        textSearchString = (EditText) findViewById(R.id.searchString);

        resultadosLV = (ListView) findViewById(R.id.resultsLV);

        List resultados = new ArrayList();
        this.resultadosLV.setAdapter(new ResultAdapter(this, resultados));

        AntidotoDatabase db = new AntidotoDatabase(this);
        //db.getReadableDatabase();
        //Fast tests
        AntidotoDatabase.checkBarcodeCursor cursor = db.checkBarcodeCursor(978156);
        /*if (cursor.getCount()>0) {
            Toast.makeText(this, "Ha encontrado algo en BD!", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "Pos no encontro nada :(", Toast.LENGTH_LONG).show();
        }
        AntidotoDatabase.checkBarcodeCursor cursor2 = db.checkBarcodeCursor(666666);
        if (cursor2.getCount()>0) {
            Toast.makeText(this, "Ha encontrado algo en BD, y no deberia", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "Pos no encontro nada, como deberia", Toast.LENGTH_LONG).show();
        }*/

    }

    private void startScan(){
        new IntentIntegrator(this).initiateScan();
    }

    private void searchString(){
        AntidotoDatabase db = new AntidotoDatabase(this);

        AntidotoDatabase.checkSearchStringCursor cursor = db.checkSearchStringCursor(textSearchString.getText().toString());
        if (cursor.getCount()>0) {

            List resultados = new ArrayList();

            try {
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    resultados.add(new Result(cursor.getColReasonsName(),
                            cursor.getColReasonsDescription(),
                            cursor.getColReasonsUrl(),
                            cursor.getColElementsName(),
                            cursor.getColElementsDescription(),
                            cursor.getColElementsBarcode(),
                            cursor.getColCompanyName()));
                }
            } finally {
                cursor.close();
            }

            this.resultadosLV.setAdapter(new ResultAdapter(this, resultados));
            resultadosLV.setVisibility(View.VISIBLE);
            //Toast.makeText(this, "Ha encontrado algo en BD!", Toast.LENGTH_LONG).show();

        }else{
            resultadosLV.setVisibility(View.GONE);
            //Toast.makeText(this, "Pos no encontro nada :(", Toast.LENGTH_LONG).show();
        }
    }

    // Get the results:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                resultadosLV.setVisibility(View.GONE);
                //Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                AntidotoDatabase db = new AntidotoDatabase(this);
                AntidotoDatabase.checkBarcodeCursor cursor = db.checkBarcodeCursor(978156);
                if (cursor.getCount()>0) {
                    List resultados = new ArrayList();

                    try {
                        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                            resultados.add(new Result(cursor.getColReasonsName(),
                                    cursor.getColReasonsDescription(),
                                    cursor.getColReasonsUrl(),
                                    cursor.getColElementsName(),
                                    cursor.getColElementsDescription(),
                                    cursor.getColElementsBarcode(),
                                    cursor.getColCompanyName()));
                        }
                    } finally {
                        cursor.close();
                    }

                    this.resultadosLV.setAdapter(new ResultAdapter(this, resultados));
                    resultadosLV.setVisibility(View.VISIBLE);
                    //Toast.makeText(this, "Ha encontrado algo en BD!", Toast.LENGTH_LONG).show();
                }else{
                    resultadosLV.setVisibility(View.GONE);
                    //Toast.makeText(this, "Pos no encontro nada :(", Toast.LENGTH_LONG).show();
                }
                //Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
