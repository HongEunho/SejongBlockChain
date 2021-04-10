package com.example.sejongblockchain;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.samsung.android.sdk.blockchain.CoinType;
import com.samsung.android.sdk.blockchain.ListenableFutureTask;
import com.samsung.android.sdk.blockchain.SBlockchain;
import com.samsung.android.sdk.blockchain.account.Account;
import com.samsung.android.sdk.blockchain.account.ethereum.EthereumAccount;
import com.samsung.android.sdk.blockchain.coinservice.CoinNetworkInfo;
import com.samsung.android.sdk.blockchain.coinservice.CoinServiceFactory;
import com.samsung.android.sdk.blockchain.coinservice.ethereum.EthereumService;
import com.samsung.android.sdk.blockchain.exception.HardwareWalletException;
import com.samsung.android.sdk.blockchain.exception.RootSeedChangedException;
import com.samsung.android.sdk.blockchain.exception.SsdkUnsupportedException;
import com.samsung.android.sdk.blockchain.network.EthereumNetworkType;
import com.samsung.android.sdk.blockchain.ui.CucumberWebView;
import com.samsung.android.sdk.blockchain.wallet.HardwareWallet;
import com.samsung.android.sdk.blockchain.wallet.HardwareWalletType;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends FragmentActivity {

    private Button connect_btn;
    private Button generate_btn;
    private Button get_btn;
    private Button pay_btn;
    private Button translist_btn;
    private Button discon_btn;
    private Button confirm_btn;
    private FloatingActionButton fab;
    private SBlockchain sBlockchain;
    private HardwareWallet wallet;
    Account generateAccount1;
    private List<Account> accounts;
    private TextView account_text;
    private BaseQuickAdapter<String, BaseViewHolder> adapter;
    private String howmuch, toaccunt;

    private CucumberWebView webView;
    private String TO_ADDRESS;
    private String RPC_ENDPOINT = "https://kovan.infura.io/v3/612239e2304b46c4926a55e6903a056e";
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        connect_btn = findViewById(R.id.connect_btn);
        generate_btn = findViewById(R.id.generate_btn);
        get_btn = findViewById(R.id.get_btn);
        pay_btn = findViewById(R.id.pay_btn);
        translist_btn = findViewById(R.id.translist_btn);
        discon_btn = findViewById(R.id.disconnect_btn);
        account_text = findViewById(R.id.account_text);
        fab = findViewById(R.id.floatingActionButton);
        confirm_btn = findViewById(R.id.confirm_account_btn);



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });


        try {
            sBlockchain = new SBlockchain();
            sBlockchain.initialize(this);
        } catch (SsdkUnsupportedException e) {
            if (e.getErrorType() == SsdkUnsupportedException.VENDOR_NOT_SUPPORTED) {
                Log.e("error", "Platform SDK is not support this device");
            }
        }

        connect_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "월렛에 연결하였습니다.", Toast.LENGTH_SHORT).show();
                connected();
            }
        });

        generate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "계좌를 발급하였습니다.", Toast.LENGTH_SHORT).show();
                generate();
            }
        });

        get_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "계좌를 불러왔습니다.", Toast.LENGTH_SHORT).show();
                getAccounts();
            }
        });

        pay_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.sendinfo, null, false);

                dlg.setView(view);

                final AppCompatEditText accountEdit = view.findViewById(R.id.questionEdit2);
                final AppCompatEditText howEdit = view.findViewById(R.id.answerEdit2);

                final Button ok_btn = view.findViewById(R.id.modify_btn);
                final Button no_btn = view.findViewById(R.id.delete_btn);

                final AlertDialog dialog = dlg.create();

                ok_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        toaccunt = accountEdit.getText().toString();
                        howmuch = howEdit.getText().toString();

                        Toast.makeText(getApplicationContext(), "송금을 시작합니다.", Toast.LENGTH_SHORT).show();
                        setPaymentsheet();
                        dialog.dismiss();
                    }
                });

                no_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });


        discon_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "연결을 해제하였습니다", Toast.LENGTH_SHORT).show();
                disconnected();
            }
        });

        translist_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Account myAccount = accounts.get(0);
                //String htmlPageUrl =

                Intent intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://kovan.etherscan.io/token/"+"0xe44def96c411ddaad28b238e72e7468f9613e8ce?a="+myAccount.getAddress()));
                startActivity(intent2);
            }
        });

        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.account_recycler, null, false);

                dlg.setView(view);

                final AppCompatEditText accountEdit = view.findViewById(R.id.questionEdit2);
                final AppCompatEditText howEdit = view.findViewById(R.id.answerEdit2);

                final Button ok_btn = view.findViewById(R.id.modify_btn);
                final Button no_btn = view.findViewById(R.id.delete_btn);

                final AlertDialog dialog = dlg.create();

                ok_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        toaccunt = accountEdit.getText().toString();
                        howmuch = howEdit.getText().toString();

                        Toast.makeText(getApplicationContext(), "송금을 시작합니다.", Toast.LENGTH_SHORT).show();
                        setPaymentsheet();
                    }
                });

                no_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
    }

    public void connected(){
        sBlockchain.getHardwareWalletManager()
                .connect(HardwareWalletType.SAMSUNG, false)
                .setCallback(new ListenableFutureTask.Callback<HardwareWallet>() {
                    @Override
                    public void onSuccess(HardwareWallet hardwareWallet) {
                        wallet = hardwareWallet;
                        System.out.println("지갑"+wallet.toString());
                    }

                    @Override
                    public void onFailure(@NotNull ExecutionException e) {
                        Throwable cause = e.getCause();
                        if (cause instanceof HardwareWalletException) {
                            // handling hardware wallet error
                        } else if (cause instanceof RootSeedChangedException) {
                            // handling root seed changed exception
                        }
                    }

                    @Override
                    public void onCancelled(@NotNull InterruptedException e) {

                    }
                });
    }

    public void disconnected(){
        sBlockchain.getHardwareWalletManager().disconnect(wallet);
    }

    private void generate(){
        CoinNetworkInfo coinNetworkInfo = new CoinNetworkInfo(
                CoinType.ETH,
                EthereumNetworkType.KOVAN,
                RPC_ENDPOINT
        );

        sBlockchain.getAccountManager()
                .generateNewAccount(wallet, coinNetworkInfo)
                .setCallback(new ListenableFutureTask.Callback<Account>() {
                    @Override
                    public void onSuccess(Account account) {
                        generateAccount1 = account;
                        System.out.println("계좌"+account.toString());
                    }

                    @Override
                    public void onFailure(@NotNull ExecutionException e) {
                        System.out.println(e.toString());
                    }

                    @Override
                    public void onCancelled(@NotNull InterruptedException e) {
                        System.out.println(e.toString());
                    }
                });
    }

    private void getAccounts(){
        accounts = sBlockchain.getAccountManager()
                .getAccounts(wallet.getWalletId(),CoinType.ETH,EthereumNetworkType.KOVAN);

        if (!accounts.isEmpty()){
            Account myAccount = accounts.get(0);
            account_text.setText(myAccount.getAddress());
        }

        System.out.println("결과"+Arrays.toString(new List[]{accounts}));
        Log.e("MyApp",Arrays.toString(new List[]{accounts}));
    }

    private void setPaymentsheet(){
        CoinNetworkInfo coinNetworkInfo = new CoinNetworkInfo(
                CoinType.ETH,
                EthereumNetworkType.KOVAN,
                RPC_ENDPOINT
        );

        EthereumService ethereumService = (EthereumService) CoinServiceFactory.getCoinService(this, coinNetworkInfo);
        Intent intent = ethereumService.createTokenPaymentSheetActivityIntent(
                this,
                wallet,
                (EthereumAccount) accounts.get(0),
                toaccunt,
                "0xe44def96c411ddaad28b238e72e7468f9613e8ce", //SejongCoin의 키
                new BigInteger(howmuch+"000000000000000000"), //단위 맞춰주기
                null
        );
        startActivityForResult(intent, 0);
    }

}