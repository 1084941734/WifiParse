package com.android.mwifi.wifi;

import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by 12701 on 2017-04-27.
 */

public class mAdapter extends RecyclerView.Adapter<mAdapter.mViewHolder>  {

    private List<Person>mPersonList;

    public mAdapter(List<Person> mPersonList) {
        this.mPersonList = mPersonList;
    }

    class mViewHolder extends RecyclerView.ViewHolder{

        private final TextView mText_name;
        private final TextView mText_password;
        private final CardView mCardView;

        public mViewHolder(View itemView) {
            super(itemView);
            mText_name = (TextView) itemView.findViewById(R.id.name);
            mText_password = (TextView) itemView.findViewById(R.id.password);
            mCardView = (CardView) itemView.findViewById(R.id.cardView);
        }
    }


    @Override
    public mViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context mContext = parent.getContext();
        View mView = LayoutInflater.from(mContext).inflate(R.layout.recycler_item, parent, false);
        final mViewHolder holder = new mViewHolder(mView);
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getLayoutPosition();
                showDialog(mContext,position);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(mViewHolder holder, int position) {
        Person p=mPersonList.get(position);
        holder.mText_name.setText(p.getName());
        holder.mText_password.setText(p.getPassword());
    }

    @Override
    public int getItemCount() {
        return mPersonList.size();
    }

    private void showDialog(final Context context, final int position){
        final String[] select=new String[]{"复制热点","复制密码","复制热点+密码"};
        AlertDialog dialog=new AlertDialog.Builder(context).setItems(select, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ClipboardManager cmb =(ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
                Person person=mPersonList.get(position);
                switch (which){
                    case 0:
                        String[] name = person.getName().split("WIFI热点:");
                        cmb.setText(name[1]);
                        new AlertDialog.Builder(context).setMessage("复制完成").show();
                        break;
                    case 1:
                        String[] password = person.getPassword().split("WIFI密码:");
                        new AlertDialog.Builder(context).setMessage("复制完成").show();
                        cmb.setText(password[1]);
                        break;
                    case 2:
                        cmb.setText(person.getName()+"\n"+person.getPassword());
                        new AlertDialog.Builder(context).setMessage("复制完成").show();
                        break;
                    default:
                        break;
                }
            }
        }).show();

    }
}
