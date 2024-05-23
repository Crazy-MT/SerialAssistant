package com.sysout.app.serial.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.FileUtils;
import com.sysout.app.serial.R;
import com.sysout.app.serial.entity.LogEntity;
import com.sysout.app.serial.ui.activity.LogDetailActivity;

import java.util.List;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.ItemHolder> {

    private Context mContext;

    private List<LogEntity> logList;

    public LogAdapter(Context context, List<LogEntity> list){
        this.mContext = context;
        this.logList = list;
    }

    @NonNull
    @Override
    public LogAdapter.ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ItemHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_log, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LogAdapter.ItemHolder viewHolder, int position) {
        LogEntity logEntity = logList.get(position);
        viewHolder.name.setText(logEntity.getName()==null?"":logEntity.getName());
        viewHolder.path.setText(logEntity.getPath()==null?"":logEntity.getPath());

        viewHolder.tvDel.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
            .setMessage(mContext.getString(R.string.serial_del_confirm)).setPositiveButton(mContext.getString(R.string.serial_commit), (dialogInterface, i) -> {
                FileUtils.delete(logEntity.getPath());
                logList.remove(position);
                notifyDataSetChanged();
            }).setNegativeButton(mContext.getString(R.string.serial_cancel), (dialogInterface, i) -> {
                dialogInterface.dismiss();
            });
            builder.create().show();
        });

        viewHolder.tvViewDetail.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, LogDetailActivity.class);
            intent.putExtra("path", logEntity.getPath());
            intent.putExtra("name", logEntity.getName());
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return logList==null?0:logList.size();
    }

    static class ItemHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView path;
        TextView tvDel;
        TextView tvViewDetail;
        ItemHolder(View item) {
            super(item);
            name = item.findViewById(R.id.item_tv_log_name);
            path = item.findViewById(R.id.item_tv_log_path);
            tvDel = item.findViewById(R.id.item_tv_del);
            tvViewDetail = item.findViewById(R.id.item_tv_view_detail);
        }
    }

}
