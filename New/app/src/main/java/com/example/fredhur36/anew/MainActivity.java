package com.example.fredhur36.anew;
import com.example.fredhur36.anew.Block;
import com.example.fredhur36.anew.StringUtil;
import com.google.gson.GsonBuilder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
//import java.util.Date;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static ArrayList<Block> blockchain = new ArrayList<Block> ();
    public static int difficulty = 0;



    //DB 의 수정, 조회시 매번 블록체인 다시 생성한다. (아직 블록체인 저장할만한 디비를 어떻게 구성하는지 잘 모르겠음
    public void make_newBlockChain(DBHelper dbHelper){
        blockchain.clear();
        String [] tmp = dbHelper.getResult().split("\n");

        //String out = "갯수 :" + tmp.length;


        if(tmp.length >0) {
            blockchain.add(new Block(tmp[0], "0"));
            blockchain.get(0).mineBlock(difficulty);
        }
            if(tmp.length > 1) {
                for (int i = 1 ; i < tmp.length; i++) {
                    blockchain.add(new Block(tmp[i], blockchain.get(blockchain.size() - 1).hash));
                    blockchain.get(i).mineBlock(difficulty);
                }
            }

            String ou = "블록체인 생성완료 !! " + blockchain.size();
            // System.out.println("\nBlockchain is Valid: " + isChainValid());
            if(isChainValid())
                Toast.makeText(MainActivity.this, ou, Toast.LENGTH_SHORT).show();


    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        //
      /*  blockchain.add(new Block("Hi im the first block", "0"));
        System.out.println("Trying to Mine block 1... ");
        blockchain.get(0).mineBlock(difficulty);

        blockchain.add(new Block("Yo im the second block",blockchain.get(blockchain.size()-1).hash));
        System.out.println("Trying to Mine block 2... ");
        blockchain.get(1).mineBlock(difficulty);

        blockchain.add(new Block("Hey im the third block",blockchain.get(blockchain.size()-1).hash));
        System.out.println("Trying to Mine block 3... ");
        blockchain.get(2).mineBlock(difficulty);

        System.out.println("\nBlockchain is Valid: " + isChainValid());
*/
        /*String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
        System.out.println("\nThe block chain: ");
        System.out.println(blockchainJson);
*/
        //
        final DBHelper dbHelper = new DBHelper(getApplicationContext(), "MoneyBook.db", null, 1);

        // 테이블에 있는 모든 데이터 출력
        final TextView result = (TextView) findViewById(R.id.result);

        final EditText etDate = (EditText) findViewById(R.id.date);
        final EditText etItem = (EditText) findViewById(R.id.item);
        final EditText etPrice = (EditText) findViewById(R.id.price);

        // 날짜는 현재 날짜로 고정
        // 현재 시간 구하기
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        // 출력될 포맷 설정
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");





       make_newBlockChain(dbHelper);

        // DB에 데이터 추가
        Button insert = (Button) findViewById(R.id.insert);
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = etDate.getText().toString();
                String item = etItem.getText().toString();
                int price = Integer.parseInt(etPrice.getText().toString());

                dbHelper.insert(date, item, price);
                result.setText(dbHelper.getResult());
                int cn = (int)dbHelper.row_cnt();
                if(dbHelper.row_cnt() == 1) {
                    blockchain.add(new Block(date, "0"));
                    System.out.println("Trying to Mine block 1... ");
                    blockchain.get(0).mineBlock(difficulty);
                    if(isChainValid()){
                        Toast.makeText(MainActivity.this, "블록체인에 저장 완료", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    blockchain.add(new Block(date, blockchain.get(blockchain.size()-1).hash));
                    blockchain.get(cn-1).mineBlock(difficulty);
                    if(isChainValid()){
                        Toast.makeText(MainActivity.this, "블록체인에 저장 완료", Toast.LENGTH_LONG).show();
                    }
                }


            }
        });

        // DB에 있는 데이터 수정
        Button update = (Button) findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item = etItem.getText().toString();
                String spec = etDate.getText().toString();
                System.out.println("here it is "+spec);
                int price = Integer.parseInt(etPrice.getText().toString());

                dbHelper.update(item, price, spec);
              //  dbHelper.update(item, price, spec);
                result.setText(dbHelper.getResult());


                String [] tmp = dbHelper.getResult().split("\n");
                System.out.println("sizeof : "+ tmp.length);
                //String out = "갯수 :" + tmp.length;

                String out = "블록 체인 갯수 :" + dbHelper.row_cnt();

                make_newBlockChain(dbHelper);
            }
        });

        // DB에 있는 데이터 삭제
        Button delete = (Button) findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item = etItem.getText().toString();

                dbHelper.delete(item);
                result.setText(dbHelper.getResult());

                make_newBlockChain(dbHelper);
            }
        });

        // DB에 있는 데이터 조회
        Button select = (Button) findViewById(R.id.select);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.setText(dbHelper.getResult());
            }
        });


        //DB 의 정보들로 만든 블록체인 갯수 생성

        Button makeBlock = (Button) findViewById(R.id.blockchain);
        makeBlock.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {


                    String ou = "블록체인 갯수 : " + (blockchain.size()-1);
                   // System.out.println("\nBlockchain is Valid: " + isChainValid());

                    Toast.makeText(MainActivity.this, ou, Toast.LENGTH_SHORT).show();



            }

        });


    }

    public static Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');

        //loop through blockchain to check hashes:
        for(int i=1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i-1);
            //compare registered hash and calculated hash:
            if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
                System.out.println("Current Hashes not equal");
                return false;
            }
            //compare previous hash and registered previous hash
            if(!previousBlock.hash.equals(currentBlock.previousHash) ) {
                System.out.println("Previous Hashes not equal");
                return false;
            }
            //check if hash is solved
            if(!currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) {
                System.out.println("This block hasn't been mined");
                return false;
            }
        }
        return true;
    }
}
