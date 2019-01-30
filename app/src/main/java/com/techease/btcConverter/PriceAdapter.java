package com.techease.btcConverter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

public class PriceAdapter extends RecyclerView.Adapter<PriceAdapter.ViewHolder> {


    private Context context;
    private List<PreviousDataModel> previousDataModels;

    public PriceAdapter(Context context, List<PreviousDataModel> previousDataModel) {
        this.context = context;
        this.previousDataModels = previousDataModel;

    }


    @NonNull
    @Override
    public PriceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.previous_price_layout, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PriceAdapter.ViewHolder viewHolder, int i) {


        PreviousDataModel previousDataModel = previousDataModels.get(i);
        viewHolder.tvPreviousPirce.setText(previousDataModel.getPreviousDatePrice());


    }

    @Override
    public int getItemCount() {
        return previousDataModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPreviousPirce;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvPreviousPirce = itemView.findViewById(R.id.tv_previous_price);


        }
    }

}
