package com.example.projekt_android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class ProductAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Product> products;
    private LayoutInflater inflater;

    public ProductAdapter(Context context, ArrayList<Product> products) {
        this.context = context;
        this.products = products;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int position) {
        return products.get(position);
    }

    @Override
    public long getItemId(int position) {
        return products.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_product, parent, false);
            holder = new ViewHolder();
            holder.tvName = convertView.findViewById(R.id.ProductName);
            holder.tvDesc = convertView.findViewById(R.id.ProductDesc);
            holder.tvPrice = convertView.findViewById(R.id.ProductPrice);
            holder.tvSales = convertView.findViewById(R.id.ProductSales);
            holder.tvDate = convertView.findViewById(R.id.ProductDate);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Product product = products.get(position);

        holder.tvName.setText(product.nazwa);
        holder.tvDesc.setText(product.opis);
        holder.tvPrice.setText(String.format(Locale.getDefault(), "%.2f zł", product.cena));
        holder.tvSales.setText(String.format(Locale.getDefault(), "Sprzedano: %d szt.", product.iloscKupionychSztuk));
        holder.tvDate.setText(product.dataDodania);

        return convertView;
    }

    static class ViewHolder {
        TextView tvName;
        TextView tvDesc;
        TextView tvPrice;
        TextView tvSales;
        TextView tvDate;
    }
}