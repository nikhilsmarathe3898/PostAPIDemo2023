package adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.localgenie.R;

import com.localgenie.utilities.AppTypeface;

import java.util.ArrayList;

public class RepeatDaysAdapter extends RecyclerView.Adapter
{

    Context mContext;
    ArrayList<String>allDaysArray;
    private ArrayList<String>selectedArrayList;
    private RepeatDaysCallback repeatDaysCallback;
    public RepeatDaysAdapter(Context mContext, ArrayList<String> allDaysArray, ArrayList<String> arrayList, RepeatDaysCallback repeatDaysCallback) {
        this.mContext = mContext;
        this.allDaysArray = allDaysArray;
        this.selectedArrayList=arrayList;
        this.repeatDaysCallback = repeatDaysCallback;
        if(selectedArrayList.size()==allDaysArray.size()){
            repeatDaysCallback.onSelectedDaysChanged(selectedArrayList);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_repeat_days,parent,false);
        return new RecyclerViewHolders(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RecyclerViewHolders hold = (RecyclerViewHolders) holder;
        hold.checkBoxDays.setText(allDaysArray.get(position));
        if(selectedArrayList.contains(allDaysArray.get(position))){
            hold.checkBoxDays.setChecked(true);
            hold.checkBoxDays.setSelected(true);
        }else{
            hold.checkBoxDays.setChecked(false);
            hold.checkBoxDays.setSelected(false);
        }
    }

    @Override
    public int getItemCount() {
        return allDaysArray == null ? 0 : allDaysArray.size();
    }

    private class RecyclerViewHolders extends RecyclerView.ViewHolder
    {
        private CheckBox checkBoxDays;
        private AppTypeface appTypeface;

        public RecyclerViewHolders(View view) {
            super(view);

            appTypeface = AppTypeface.getInstance(mContext);
            checkBoxDays = view.findViewById(R.id.checkBoxDays);
            checkBoxDays.setTypeface(appTypeface.getHind_regular());
            checkBoxDays.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(checkBoxDays.isChecked()) {
                        checkBoxDays.setSelected(true);
                        selectedArrayList.add(allDaysArray.get(getAdapterPosition()));
                        repeatDaysCallback.onSelectedDaysChanged(selectedArrayList);
                    }else {
                        checkBoxDays.setSelected(false);
                        selectedArrayList.remove(allDaysArray.get(getAdapterPosition()));
                        repeatDaysCallback.onSelectedDaysChanged(selectedArrayList);
                    }
                }
            });
        }
    }
    public interface RepeatDaysCallback
    {
        void onSelectedDaysChanged(ArrayList<String> selectList);
    }
}