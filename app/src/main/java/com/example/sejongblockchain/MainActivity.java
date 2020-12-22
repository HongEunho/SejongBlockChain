package com.example.sejongblockchain;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends FragmentActivity {

    private Button connect_btn;
    private Button generate_btn;
    private Button get_btn;
    private Button pay_btn;
    private Button send_btn;
    private Button discon_btn;
    private FloatingActionButton fab;
    private SBlockchain sBlockchain;
    private HardwareWallet wallet;
    Account generateAccount1;
    private List<Account> accounts;
    private CucumberWebView webView;
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
        send_btn = findViewById(R.id.send_btn);
        discon_btn = findViewById(R.id.disconnect_btn);
        fab = findViewById(R.id.floatingActionButton);

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
                Toast.makeText(getApplicationContext(), "Connect버튼이 눌렸습니다.", Toast.LENGTH_SHORT).show();
                connected();
            }
        });

        generate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "generate버튼이 눌렸습니다.", Toast.LENGTH_SHORT).show();
                generate();
            }
        });

        get_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "getAccount버튼이 눌렸습니다.", Toast.LENGTH_SHORT).show();
                getAccounts();
            }
        });

        pay_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Payment버튼이 눌렸습니다.", Toast.LENGTH_SHORT).show();
                setPaymentsheet();
            }
        });

        discon_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Disconnect버튼이 눌렸습니다.", Toast.LENGTH_SHORT).show();
                disconnected();
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
                "0xE64732E0A0982FBa5721FeB34c97bc0f905a4fcC",
                "0xe44def96c411ddaad28b238e72e7468f9613e8ce",
                new BigInteger("10000000000000"),
                null
        );
        startActivityForResult(intent, 0);
    }
}