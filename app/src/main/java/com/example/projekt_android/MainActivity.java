package com.example.projekt_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    ProductAdapter adapter;
    ArrayList<Product> productList;
    EditText etName, etDesc, etPrice;
    String username;
    int userId;
    TextView tvWelcome, tvProductCount;
    Spinner spinnerSort;
    String currentSort = "date-desc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        username = getIntent().getStringExtra("username");
        userId = getIntent().getIntExtra("userId", -1);

        if (username == null || userId == -1) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        listView = findViewById(R.id.listProducts);
        productList = new ArrayList<>();

        tvWelcome = findViewById(R.id.tvWelcome);
        tvWelcome.setText("Witaj, " + username);

        tvProductCount = findViewById(R.id.tvProductCount);

        spinnerSort = findViewById(R.id.spinnerSort);
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(this,
                R.array.sort_options, android.R.layout.simple_spinner_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(sortAdapter);

        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: currentSort = "date-desc"; break;
                    case 1: currentSort = "date-asc"; break;
                    case 2: currentSort = "sales-desc"; break;
                    case 3: currentSort = "sales-asc"; break;
                    case 4: currentSort = "price-asc"; break;
                    case 5: currentSort = "price-desc"; break;
                }
                sortAndDisplayProducts();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        etName = findViewById(R.id.etName);
        etDesc = findViewById(R.id.etDesc);
        etPrice = findViewById(R.id.etPrice);

        Button btnAdd = findViewById(R.id.btnAdd);
        Button btnLogout = findViewById(R.id.btnLogout);

        btnAdd.setOnClickListener(v -> {
            String nazwa = etName.getText().toString().trim();
            String opis = etDesc.getText().toString().trim();
            String cenaText = etPrice.getText().toString().trim();

            if (nazwa.isEmpty() || opis.isEmpty() || cenaText.isEmpty()) {
                Toast.makeText(this, "Uzupełnij wszystkie pola", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double cena = Double.parseDouble(cenaText);
                if (cena <= 0) {
                    Toast.makeText(this, "Cena musi być większa od 0", Toast.LENGTH_SHORT).show();
                    return;
                }

                ApiService.addProduct(nazwa, opis, cena, userId, new ApiService.ApiCallback() {
                    @Override
                    public void onSuccess(String result) {
                            etName.setText("");
                            etDesc.setText("");
                            etPrice.setText("");
                            loadProducts();
                    }

                    @Override
                    public void onError(String error) {
                                Toast.makeText(MainActivity.this, "Błąd: " + error, Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Podaj poprawną cenę", Toast.LENGTH_SHORT).show();
            }
        });

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        loadProducts();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Product product = productList.get(position);
            showProductMenu(product);
        });
    }

    private void showProductMenu(Product product) {
        new AlertDialog.Builder(this)
                .setTitle(product.nazwa)
                .setItems(new String[]{"Kup produkt", "Edytuj produkt", "Usuń produkt"}, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            buyProduct(product);
                            break;
                        case 1:
                            showEditDialog(product);
                            break;
                        case 2:
                            showDeleteDialog(product);
                            break;
                    }
                })
                .show();
    }

    private void buyProduct(Product product) {
        ApiService.buyProduct(product.id, new ApiService.ApiCallback() {
            @Override
            public void onSuccess(String result) {
                    loadProducts();
            }

            @Override
            public void onError(String error) {
                        Toast.makeText(MainActivity.this, "Błąd zakupu: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditDialog(Product product) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_product, null);
        EditText etEditName = dialogView.findViewById(R.id.etEditName);
        EditText etEditDesc = dialogView.findViewById(R.id.etEditDesc);
        EditText etEditPrice = dialogView.findViewById(R.id.etEditPrice);

        etEditName.setText(product.nazwa);
        etEditDesc.setText(product.opis);
        etEditPrice.setText(String.valueOf(product.cena));

        new AlertDialog.Builder(this)
                .setTitle("Edytuj produkt")
                .setView(dialogView)
                .setPositiveButton("Zapisz", (dialog, which) -> {
                    String newName = etEditName.getText().toString().trim();
                    String newDesc = etEditDesc.getText().toString().trim();
                    String newPriceText = etEditPrice.getText().toString().trim();

                    if (newName.isEmpty() || newDesc.isEmpty() || newPriceText.isEmpty()) {
                        Toast.makeText(this, "Wypełnij wszystkie pola", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        double newPrice = Double.parseDouble(newPriceText);
                        if (newPrice <= 0) {
                            Toast.makeText(this, "Cena musi być większa od 0", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        ApiService.updateProduct(product.id, newName, newDesc, newPrice, userId,
                                new ApiService.ApiCallback() {
                                    @Override
                                    public void onSuccess(String result) {
                                            loadProducts();
                                    }

                                    @Override
                                    public void onError(String error) {
                                                Toast.makeText(MainActivity.this, "Błąd aktualizacji: " + error, Toast.LENGTH_SHORT).show();
                                    }
                                });

                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Podaj poprawną cenę", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Anuluj", null)
                .show();
    }

    private void showDeleteDialog(Product product) {
        new AlertDialog.Builder(this)
                .setTitle("Usuwanie produktu")
                .setMessage("Czy na pewno chcesz usunąć \"" + product.nazwa + "\"?")
                .setPositiveButton("Tak", (dialog, which) -> {
                    ApiService.deleteProduct(product.id, userId, new ApiService.ApiCallback() {
                        @Override
                        public void onSuccess(String result) {
                                loadProducts();
                        }

                        @Override
                        public void onError(String error) {
                                    Toast.makeText(MainActivity.this, "Błąd usuwania: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Nie", null)
                .show();
    }

    private void loadProducts() {
        ApiService.getProducts(userId, new ApiService.ProductsCallback() {
            @Override
            public void onSuccess(ArrayList<Product> products) {
                    productList.clear();
                    productList.addAll(products);
                    sortAndDisplayProducts();
            }

            @Override
            public void onError(String error) {
                        Toast.makeText(MainActivity.this, "Błąd pobierania produktów: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sortAndDisplayProducts() {
        switch (currentSort) {
            case "date-desc":
                Collections.sort(productList, (p1, p2) -> p2.dataDodania.compareTo(p1.dataDodania));
                break;
            case "date-asc":
                Collections.sort(productList, (p1, p2) -> p1.dataDodania.compareTo(p2.dataDodania));
                break;
            case "sales-desc":
                Collections.sort(productList, (p1, p2) -> Integer.compare(p2.iloscKupionychSztuk, p1.iloscKupionychSztuk));
                break;
            case "sales-asc":
                Collections.sort(productList, (p1, p2) -> Integer.compare(p1.iloscKupionychSztuk, p2.iloscKupionychSztuk));
                break;
            case "price-asc":
                Collections.sort(productList, (p1, p2) -> Double.compare(p1.cena, p2.cena));
                break;
            case "price-desc":
                Collections.sort(productList, (p1, p2) -> Double.compare(p2.cena, p1.cena));
                break;
        }

        adapter = new ProductAdapter(this, productList);
        listView.setAdapter(adapter);
        tvProductCount.setText("Produkty");
    }
}

class Product {
    int id;
    String nazwa;
    String opis;
    double cena;
    int iloscKupionychSztuk;
    String dataDodania;

    public Product(int id, String nazwa, String opis, double cena, int iloscKupionychSztuk, String dataDodania) {
        this.id = id;
        this.nazwa = nazwa;
        this.opis = opis;
        this.cena = cena;
        this.iloscKupionychSztuk = iloscKupionychSztuk;
        this.dataDodania = dataDodania;
    }
}