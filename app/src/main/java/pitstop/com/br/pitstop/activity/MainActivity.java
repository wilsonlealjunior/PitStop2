package pitstop.com.br.pitstop.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SearchView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;



import java.util.ArrayList;

import pitstop.com.br.pitstop.R;
import pitstop.com.br.pitstop.model.Produto;
import pitstop.com.br.pitstop.adapter.LstViewTabelaEstoqueAdpter;

public class MainActivity extends AppCompatActivity {
    ArrayList<Produto> produtos = new ArrayList<>();
    LstViewTabelaEstoqueAdpter adapter;
    LstViewTabelaEstoqueAdpter adapterPesquisa;
    Button novaVenda;
    private ListView listaDeProdutos;
    ArrayList<Produto> pesquisa = new ArrayList<>();
    private SearchView textPesquisa;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        textPesquisa= (SearchView) findViewById(R.id.textPesquisa);
        novaVenda = (Button)findViewById(R.id.nova_venda);
        listaDeProdutos = (ListView) findViewById(R.id.lista_de_produto);
        ViewGroup headerView = (ViewGroup)getLayoutInflater().inflate(R.layout.header_estoque, listaDeProdutos,false);
        listaDeProdutos.addHeaderView(headerView);



        adapter =new LstViewTabelaEstoqueAdpter(this,R.layout.tabela_estoque,R.id.produto,produtos);
        adapterPesquisa =new LstViewTabelaEstoqueAdpter(this,R.layout.tabela_estoque,R.id.produto,pesquisa);
        //pesquisar();



        textPesquisa.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchQuery) {
                pesquisar(searchQuery.toString().trim());


                listaDeProdutos.setAdapter(adapterPesquisa);

//                textPesquisa.invalidate();
                return true;
            }
        });


        novaVenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v)  {

//                auth.signOut();
                //comentar para testar o webservice
                Intent intentVaiProFormulario = new Intent(MainActivity.this, CadastrarVendasActivity.class);
                startActivity(intentVaiProFormulario);

            }
        });


        //registerForContextMenu(listaDeProdutos);

//        databaseReference = FirebaseDatabase.getInstance().getReference();
//        databaseReference.child("Produtos").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                Log.i("LOG","->>" + dataSnapshot);
//                produtos.clear();
//                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                    Log.i("LOG", postSnapshot.getKey());
//                    Produto campoProduto = postSnapshot.getValue(Produto.class);
//                    Log.i("LOG", campoProduto.getNome());
//                        produtos.add(campoProduto);
//
//                }
//
//                listaDeProdutos.setAdapter(adapter);
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//                Log.w("LOG", "Failed to read value.", error.toException());
//            }
//        });



    }

    public void pesquisar(String txtPesquisa) {
        int textlength = txtPesquisa.length();
        pesquisa.clear();

        for (int i = 0; i < produtos.size(); i++) {
            if (textlength <= produtos.get(i).getNome().length()) {
                if (txtPesquisa.equalsIgnoreCase((String) produtos.get(i).getNome().subSequence(0, textlength))) {
                    pesquisa.add(produtos.get(i));
                }
            }
        }
    }




//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, final ContextMenu.ContextMenuInfo menuInfo)  {
//        MenuItem deletar = menu.add("Deletar");
//        MenuItem editar = menu.add("Editar");
//        MenuItem entradaDeProduto = menu.add("Entrada de Produto");
//        MenuItem saidaDeProduto = menu.add("Saida de Produto");
//        deletar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
//                Produto campoProduto = (Produto) listaDeProdutos.getItemAtPosition(info.position);
//                databaseReference = FirebaseDatabase.getInstance().getReference();
//                databaseReference.child("Produtos").child(campoProduto.getId()).removeValue();
//                Toast.makeText(MainActivity.this, "Deletar o campoProduto " + campoProduto.getNome(), Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        });
//        editar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//
//
//                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
//                Produto campoProduto = (Produto) listaDeProdutos.getItemAtPosition(info.position);
//                Intent intentVaiProFormulario = new Intent(MainActivity.this, CadastroProdutoActivity.class);
//                intentVaiProFormulario.putExtra("campoProduto", campoProduto);
//                startActivity(intentVaiProFormulario);
//                Toast.makeText(MainActivity.this, "Editar o campoProduto " + campoProduto.getNome(), Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        });
//        entradaDeProduto.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//
//
//                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
//                Produto campoProduto = (Produto) listaDeProdutos.getItemAtPosition(info.position);
//                Intent intentVaiProFormulario = new Intent(MainActivity.this, movimentacaoProdutoActivity.class);
//                intentVaiProFormulario.putExtra("campoProduto", campoProduto);
//                intentVaiProFormulario.putExtra("movimentacao","entrada");
//                startActivity(intentVaiProFormulario);
//                Toast.makeText(MainActivity.this, "entrada no campoProduto " + campoProduto.getNome(), Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        });
//        saidaDeProduto.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
//                Produto campoProduto = (Produto) listaDeProdutos.getItemAtPosition(info.position);
//                Intent intentVaiProFormulario = new Intent(MainActivity.this, movimentacaoProdutoActivity.class);
//                intentVaiProFormulario.putExtra("campoProduto", campoProduto);
//                intentVaiProFormulario.putExtra("movimentacao","saida");
//                startActivity(intentVaiProFormulario);
//                Toast.makeText(MainActivity.this, "saida no campoProduto " + campoProduto.getNome(), Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        });
//
//
//    }


//        adicionarProduto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                databaseReference = FirebaseDatabase.getInstance().getReference();
//                String key  = databaseReference.push().getKey();
//                databaseReference.child("Produtos").child(key).setValue(new Produto(campo1.getText().toString(),4.0,3));
//            }
//        });
//        alterar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                    prod.setPreco(50.0);
//                    databaseReference.child("Produtos").child(chave).setValue(prod);
//
//            }
//        });




//        listar = (Button) findViewById(R.id.listar);
//        listar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                databaseReference = FirebaseDatabase.getInstance().getReference();
//                databaseReference.child("Produtos").addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//
//                        Log.i("LOG","->>" + dataSnapshot);
//                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                            Log.i("LOG", postSnapshot.getKey());
//                            Produto campoProduto = postSnapshot.getValue(Produto.class);
//                            Log.i("LOG", campoProduto.getNome());
//                            if(campoProduto.getNome().equals("gus")){
//                                chave = postSnapshot.getKey();
//                                prod = new Produto();
//                                prod.setPreco(campoProduto.getPreco());
//                                prod.setNome(campoProduto.getNome());
//                                prod.setQuantidade(campoProduto.getQuantidade());
//
//                            }
//
//
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError error) {
//                        // Failed to read value
//                        Log.w("LOG", "Failed to read value.", error.toException());
//                    }
//                });
//
//            }
//        });
//    }
}
