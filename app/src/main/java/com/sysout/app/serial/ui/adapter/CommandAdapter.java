package com.sysout.app.serial.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sysout.app.serial.R;
import com.sysout.app.serial.entity.SerialCommand;

import java.util.ArrayList;
import java.util.List;

public class CommandAdapter extends RecyclerView.Adapter<CommandAdapter.ItemHolder> {

    private Context mContext;

    private List<SerialCommand> commandList;
    private OnClickListener onClickListener;

    private List<SerialCommand> selectCommandList = new ArrayList<>();

    public CommandAdapter(Context context, List<SerialCommand> list){
        this.mContext = context;
        this.commandList = list;
    }

    public void setOnClickListener(OnClickListener clickListener){
        this.onClickListener = clickListener;
    }

    @NonNull
    @Override
    public CommandAdapter.ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ItemHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_command, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CommandAdapter.ItemHolder viewHolder, int i) {
        SerialCommand serialCommand = commandList.get(i);
        viewHolder.mark.setText(serialCommand.getRemarks());
        viewHolder.commData.setText(serialCommand.getCommand());
        viewHolder.checkComm.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if(selectCommandList.size() >= 6){
                    Toast.makeText(mContext, "最多可选择6条指令！", Toast.LENGTH_SHORT).show();
                    viewHolder.checkComm.setChecked(false);
                }else {
                    selectCommandList.add(serialCommand);
                }
            } else {
                selectCommandList.remove(serialCommand);
            }
            if(null != onClickListener) onClickListener.onCheckedChanged(buttonView, isChecked, selectCommandList);
        });
        viewHolder.editComm.setOnClickListener(v -> {
            if(null != onClickListener) onClickListener.onEditClick(serialCommand);
        });
        viewHolder.delComm.setOnClickListener(v -> {
            if(null != onClickListener) onClickListener.onDelClick(serialCommand.getId());
        });
    }

    @Override
    public int getItemCount() {
        return commandList==null?0:commandList.size();
    }

    static class ItemHolder extends RecyclerView.ViewHolder {
        CheckBox checkComm;
        TextView mark;
        TextView commData;
        TextView editComm;
        TextView delComm;
        ItemHolder(View item) {
            super(item);
            checkComm = item.findViewById(R.id.serial_item_cb_check_comm);
            mark = item.findViewById(R.id.serial_item_tv_mark);
            commData = item.findViewById(R.id.serial_item_tv_comm);
            editComm = item.findViewById(R.id.serial_item_tv_edit);
            delComm = item.findViewById(R.id.serial_item_tv_del);
        }
    }

    public interface OnClickListener {
        void onCheckedChanged(CompoundButton buttonView, boolean isChecked, List<SerialCommand> selectCommandList);
        void onEditClick(SerialCommand serialCommand);
        void onDelClick(long id);
    }
}
