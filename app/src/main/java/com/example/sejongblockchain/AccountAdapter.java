package com.example.sejongblockchain;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/*public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountViewHolder> {

    private List<Account> mList;

    public AccountAdapter(List<Account> list){this.mList = list;}

    public class AccountViewHolder extends RecyclerView.ViewHolder{
        protected TextView account;
        protected Button ok_btn;

        public AccountViewHolder(@NonNull View itemView) {
            super(itemView);
            this.account = itemView.findViewById(R.id.department);
            this.content = itemView.findViewById(R.id.content);
            this.remove_btn = itemView.findViewById(R.id.remove_btn);

            remove_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if(position != RecyclerView.NO_POSITION){
                        if(mListener != null)
                            mListener.onDeleteClick(v, position);
                    }
                }
            });

        }
    }
}*/
