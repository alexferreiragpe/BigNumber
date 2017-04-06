package com.example.olvia.bignumber;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements Runnable {

    public int valorBotao1Random, valorBotao2Random, TotalPontos, Recorde, ValorBotaoRecorde;
    ProgressBar pb;
    Thread td;
    Handler hd;
    int it;
    SQLiteDatabase db;
    List<Integer> dados = new ArrayList();
    private Button BtnValor1, BtnValor2, Recomecar;
    private TextView TxtPontuacao, recordes, TxtValorBotaoRecorde;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        db = openOrCreateDatabase("BigNumber", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        db.setVersion(1);
        db.setLocale(Locale.getDefault());
        db.setLockingEnabled(true);
        db.execSQL("CREATE TABLE IF NOT EXISTS Recorde(_id INTEGER PRIMARY KEY AUTOINCREMENT, Record INTEGER)");

        recordes = (TextView) findViewById(R.id.txtRecorde);


        long numOfEntries = DatabaseUtils.queryNumEntries(db, "Recorde");

        if (numOfEntries == 0) {
            // Tabela vazia, preencha com seus dados iniciais
            recordes.setText("0");
            Toast.makeText(getApplicationContext(), "Bem Vindo, Aproveite! \nNenhum Recorde Registrado", Toast.LENGTH_LONG).show();

        } else {
            // Tabela ja contem dados.

            String selectQuery = "SELECT Record FROM Recorde";

            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    int valor = Integer.parseInt(cursor.getString(cursor.getColumnIndex("Record")).toString());
                    dados.add(valor);
                } while (cursor.moveToNext());
            }

            int valorarray = dados.get(0);

            for (int i = 0; i < dados.size(); i++) {
                if (dados.get(i) > valorarray) {
                    valorarray = dados.get(i);

                }
            }

            recordes.setText(String.format(String.valueOf(valorarray)));
        }

        BtnValor1 = (Button) findViewById(R.id.btnNumero1);
        BtnValor2 = (Button) findViewById(R.id.btnNumero2);
        TxtPontuacao = (TextView) findViewById(R.id.txtPonto);
        Recomecar = (Button) findViewById(R.id.BtnRecomecar);
        TxtValorBotaoRecorde = (TextView) findViewById(R.id.txtRecorde);
        Recomecar.setEnabled(false);

        TotalPontos = 0;
        Recorde = 0;
        ValorBotaoRecorde = 0;

        valorBotao1Random = gerarAleatorio() + 1;
        valorBotao2Random = gerarAleatorio() + 1;
        while (valorBotao1Random == valorBotao2Random) {
            valorBotao1Random = gerarAleatorio() + 1;
            valorBotao2Random = gerarAleatorio() + 1;
        }
        BtnValor1.setText(String.valueOf(valorBotao1Random));
        BtnValor2.setText(String.valueOf(valorBotao2Random));
        TxtPontuacao.setText(String.valueOf("Pontuação: " + TotalPontos));
        pb = (ProgressBar) findViewById(R.id.pbstatus);


    }

    private int gerarAleatorio() {
        if (TotalPontos < 10) {
            return (int) (Math.random() * 10 + 1);
        } else if (TotalPontos >= 10 && TotalPontos < 100) {
            return (int) (Math.random() * 100 + 1);
        } else if (TotalPontos >= 100 && TotalPontos <= 1000) {
            return (int) (Math.random() * 1000 + 1);
        }
        return 0;
    }

    public void Recomecar(View v) {
        BtnValor1.setEnabled(true);
        BtnValor2.setEnabled(true);
        TotalPontos = 0;
        pb.setProgress(0);
        TxtPontuacao.setText("Pontuação: " + TotalPontos);

        valorBotao1Random = gerarAleatorio() + 1;
        valorBotao2Random = gerarAleatorio() + 1;
        while (valorBotao1Random == valorBotao2Random) {
            valorBotao1Random = gerarAleatorio() + 1;
            valorBotao2Random = gerarAleatorio() + 1;
        }

        BtnValor1.setText(String.valueOf(valorBotao1Random));
        BtnValor2.setText(String.valueOf(valorBotao2Random));
        Recomecar.setEnabled(false);

    }

    @Override
    public void run() {
        it = 0;

        try {
            while (it <= 100) {
                Thread.sleep(300);
                hd.post(new Runnable() {
                    @Override
                    public void run() {
                        pb.setProgress(it++);

                        if (it == 100) {

                            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                            ValorBotaoRecorde = Integer.parseInt(TxtValorBotaoRecorde.getText().toString());
                            alertDialog.setTitle("Perdeu...");
                            alertDialog.setMessage("Seu Tempo Acabou! \n\nVocê fez " + TotalPontos + " pontos.");
                            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();

                                }
                            });
                            alertDialog.setIcon(R.drawable.icon);
                            alertDialog.show();

                            ValorBotaoRecorde = Integer.parseInt(TxtValorBotaoRecorde.getText().toString());

                            if (ValorBotaoRecorde >= TotalPontos) {
                                db.execSQL("INSERT INTO Recorde(Record) VALUES('" + ValorBotaoRecorde + "')");
                            } else if (ValorBotaoRecorde < TotalPontos) {
                                db.execSQL("INSERT INTO Recorde(Record) VALUES('" + TotalPontos + "')");
                            }

                            if (TotalPontos <= 0) {
                                TxtPontuacao.setText("Pontuação: 0");
                            }

                            String selectQuery = "SELECT Record FROM Recorde";

                            Cursor cursor = db.rawQuery(selectQuery, null);

                            if (cursor.moveToFirst()) {
                                do {
                                    int valor = Integer.parseInt(cursor.getString(cursor.getColumnIndex("Record")).toString());
                                    dados.add(valor);
                                } while (cursor.moveToNext());
                            }

                            int valorarray = dados.get(0);

                            for (int i = 0; i < dados.size(); i++) {
                                if (dados.get(i) > valorarray) {
                                    valorarray = dados.get(i);

                                }
                            }

                            TxtValorBotaoRecorde.setText(String.valueOf(valorarray));
                            BtnValor1.setEnabled(false);
                            BtnValor2.setEnabled(false);
                            Recomecar.setEnabled(true);
                            TotalPontos = 0;
                            pb.setProgress(0);
                            td.interrupt();


                        }
                    }
                });
            }

        } catch (Exception e) {
        }

    }

    public void clickBotao1(View v) {

        if (valorBotao1Random > valorBotao2Random) {
            TotalPontos = TotalPontos + 1;

        } else if (valorBotao1Random < valorBotao2Random) {
            TotalPontos = TotalPontos - 1;

        }

        if (TotalPontos <= 0) {

            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
            alertDialog.setTitle("Perdeu...");
            alertDialog.setMessage("Seus Pontos Acabaram!");
            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();

                }
            });
            alertDialog.setIcon(R.drawable.icon);
            alertDialog.show();
            pb.setProgress(0);
            td.interrupt();
            Recorde = TotalPontos;
            BtnValor1.setEnabled(false);
            BtnValor2.setEnabled(false);
            TxtPontuacao.setText("Pontuação: 0");
            Recomecar.setEnabled(true);
        } else {
            valorBotao1Random = Integer.parseInt(BtnValor1.getText().toString());
            valorBotao2Random = Integer.parseInt(BtnValor2.getText().toString());
            hd = new Handler();
            td = new Thread(MainActivity.this);
            td.start();

            TxtPontuacao.setText("Pontuação: " + TotalPontos);
            valorBotao1Random = gerarAleatorio() + 1;
            valorBotao2Random = gerarAleatorio() + 1;
            while (valorBotao1Random == valorBotao2Random) {
                valorBotao1Random = gerarAleatorio() + 1;
                valorBotao2Random = gerarAleatorio() + 1;
            }

            BtnValor1.setText(String.valueOf(valorBotao1Random));
            BtnValor2.setText(String.valueOf(valorBotao2Random));
        }

    }

    public void clickBotao2(View v) {


        if (valorBotao2Random > valorBotao1Random) {
            TotalPontos = TotalPontos + 1;

        } else if (valorBotao2Random < valorBotao1Random) {
            TotalPontos = TotalPontos - 1;

        }


        if (TotalPontos <= 0) {

            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
            alertDialog.setTitle("Perdeu...");
            alertDialog.setMessage("Seus Pontos Acabaram!");
            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();

                }
            });
            alertDialog.setIcon(R.drawable.icon);
            alertDialog.show();
            pb.setProgress(0);
            td.interrupt();
            BtnValor1.setEnabled(false);
            BtnValor2.setEnabled(false);
            Recorde = TotalPontos;
            TxtPontuacao.setText("Pontuação: 0");
            Recomecar.setEnabled(true);
        } else {
            valorBotao1Random = Integer.parseInt(BtnValor1.getText().toString());
            valorBotao2Random = Integer.parseInt(BtnValor2.getText().toString());
            hd = new Handler();
            td = new Thread(MainActivity.this);
            td.start();


            TxtPontuacao.setText("Pontuação: " + TotalPontos);
            valorBotao1Random = gerarAleatorio() + 1;
            valorBotao2Random = gerarAleatorio() + 1;
            while (valorBotao1Random == valorBotao2Random) {
                valorBotao1Random = gerarAleatorio() + 1;
                valorBotao2Random = gerarAleatorio() + 1;
            }

            BtnValor1.setText(String.valueOf(valorBotao1Random));
            BtnValor2.setText(String.valueOf(valorBotao2Random));
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(getApplicationContext(), "Sobre", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MainActivity.this, Main2ActivitySobre.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
