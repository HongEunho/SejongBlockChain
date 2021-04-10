package com.example.sejongblockchain;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.samsung.android.sdk.blockchain.CoinType;
import com.samsung.android.sdk.blockchain.ListenableFutureTask;
import com.samsung.android.sdk.blockchain.SBlockchain;
import com.samsung.android.sdk.blockchain.account.Account;
import com.samsung.android.sdk.blockchain.account.AccountManager;
import com.samsung.android.sdk.blockchain.account.ethereum.EthereumAccount;
import com.samsung.android.sdk.blockchain.coinservice.CoinNetworkInfo;
import com.samsung.android.sdk.blockchain.coinservice.CoinServiceFactory;
import com.samsung.android.sdk.blockchain.coinservice.ethereum.EthereumService;
import com.samsung.android.sdk.blockchain.exception.HardwareWalletException;
import com.samsung.android.sdk.blockchain.exception.RootSeedChangedException;
import com.samsung.android.sdk.blockchain.exception.SsdkUnsupportedException;
import com.samsung.android.sdk.blockchain.network.EthereumNetworkType;
import com.samsung.android.sdk.blockchain.wallet.HardwareWallet;
import com.samsung.android.sdk.blockchain.wallet.HardwareWalletManager;
import com.samsung.android.sdk.blockchain.wallet.HardwareWalletType;

import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class TransactionActivity extends AppCompatActivity {

    private final static String TAG = TransactionActivity.class.getSimpleName();
    private Context mContext;
    private TextView accountTv;
    private String TO_ADDRESS;
    private String RPC_ENDPOINT = "https://kovan.infura.io/v3/612239e2304b46c4926a55e6903a056e";
    private ProgressDialog mProgressDialog;
    private SBlockchain sBlockchain;
    private AccountManager mAccountManager;
    private HardwareWalletManager mHardwareWalletManager;
    private CoinNetworkInfo mCoinNetworkInfo;
    private Handler handler = new Handler();
    private HardwareWallet wallet;
    private List<Account> accounts;
    private EthereumAccount myAccount;
    private Button generate_btn;
    private Button modify_btn;
    private Button transition_btn;
    private Button confirm_btn;
    private Switch connectSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        mProgressDialog = new ProgressDialog(mContext);
        accountTv = findViewById(R.id.account_tv);

        generate_btn = findViewById(R.id.generate_btn2);
        modify_btn = findViewById(R.id.modify_btn);
        transition_btn = findViewById(R.id.transition_btn);
        confirm_btn = findViewById(R.id.confirm_btn);

        generate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateNewAccount();
            }
        });

        transition_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPayment();
            }
        });


        connectSwitch = findViewById(R.id.connect_switch);
        connectSwitch.setChecked(false);

        connectSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    connected();
                }
                else{
                    disconnected();
                }
            }
        });

        try {
            init();
        } catch (SsdkUnsupportedException e) {
            e.printStackTrace();
        }
        connected();
    }

    private void init() throws SsdkUnsupportedException {
        sBlockchain = new SBlockchain();
        sBlockchain.initialize(mContext);
        mAccountManager = sBlockchain.getAccountManager();
        mHardwareWalletManager = sBlockchain.getHardwareWalletManager();
        mCoinNetworkInfo =
                new CoinNetworkInfo(
                        CoinType.ETH,
                        EthereumNetworkType.KOVAN,
                        RPC_ENDPOINT
                );
        connected();
    }

    public void connected(){
        mProgressDialog.setMessage("연결하는 중입니다");
        mProgressDialog.show();

        mHardwareWalletManager
                .connect(HardwareWalletType.SAMSUNG, false)
                .setCallback(new ListenableFutureTask.Callback<HardwareWallet>() {
                    @Override
                    public void onSuccess(HardwareWallet hardwareWallet) {
                        wallet = hardwareWallet;
                        System.out.println("지갑"+wallet.toString());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                mProgressDialog.dismiss();
                                getAccount();
                            }
                        });
                    }

                    @Override
                    public void onFailure(@NotNull ExecutionException e) {
                        mProgressDialog.dismiss();
                        Throwable cause = e.getCause();
                        if (cause instanceof HardwareWalletException) {
                            // handling hardware wallet error
                        } else if (cause instanceof RootSeedChangedException) {
                            // handling root seed changed exception
                        }
                    }

                    @Override
                    public void onCancelled(@NotNull InterruptedException e) {
                        mProgressDialog.dismiss();
                    }
                });
    }

    public void disconnected(){

    }

    public void getAccount(){
        accounts = mAccountManager.getAccounts(wallet.getWalletId(),CoinType.ETH,EthereumNetworkType.KOVAN);

        if (!accounts.isEmpty()) {
            myAccount = (EthereumAccount) accounts.get(accounts.size() - 1);
            accountTv.setText(myAccount.getAddress());
            generate_btn.setBackgroundColor(Color.GRAY);
            generate_btn.setText("발급 완료");
            generate_btn.setEnabled(false);
        }

        Log.e(TAG, Arrays.toString(new List[]{accounts}));
    }

    public void setPayment(){
        EthereumService service =
                (EthereumService) CoinServiceFactory.getCoinService(mContext, mCoinNetworkInfo);

        Intent intent =
                service.createEthereumPaymentSheetActivityIntent(
                        mContext,
                        wallet,
                        myAccount,
                        TO_ADDRESS,
                        new BigInteger("100000000000000"),
                        null,
                        null
                );
        startActivityForResult(intent, 0);
    }

    private AsyncTask<Void, Void, Account> generateNewAccount() {
        return new AsyncTask<Void, Void, Account>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressDialog.setMessage("Generating...");
                mProgressDialog.show();
            }

            @Override
            protected Account doInBackground(Void... voids) {

                if (wallet != null) {
                    try {
                        mAccountManager.generateNewAccount(wallet, mCoinNetworkInfo).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Account account) {
                super.onPostExecute(account);
                mProgressDialog.dismiss();

                Log.i(TAG, "New account is generated. Address is " + account.getAddress());
                getAccount();
            }
        };
    }
}