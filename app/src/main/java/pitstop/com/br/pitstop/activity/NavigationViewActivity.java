package pitstop.com.br.pitstop.activity;
/**
 * Created by wilso on 19/10/2017.
 */


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import de.greenrobot.event.EventBus;
import pitstop.com.br.pitstop.LoginActivity;
import pitstop.com.br.pitstop.R;
import pitstop.com.br.pitstop.activity.cadastro.CadastrarVendasActivity;
import pitstop.com.br.pitstop.activity.cadastro.CadastroAvariaActivity;
import pitstop.com.br.pitstop.activity.cadastro.CadastroEntradaProdutoActivity;
import pitstop.com.br.pitstop.activity.cadastro.CadastroFuroActivity;
import pitstop.com.br.pitstop.activity.cadastro.CadastroMovimentacaoProdutoActivity;
import pitstop.com.br.pitstop.activity.relatorio.RelatorioAvariaActivity;
import pitstop.com.br.pitstop.activity.relatorio.RelatorioEntradaProdutoActivity;
import pitstop.com.br.pitstop.activity.relatorio.RelatorioFuroActivity;
import pitstop.com.br.pitstop.activity.relatorio.RelatorioGeralActivity;
import pitstop.com.br.pitstop.activity.relatorio.RelatorioMovimentacaoProdutoActivity;
import pitstop.com.br.pitstop.activity.relatorio.RelatorioVendasActivity;
import pitstop.com.br.pitstop.event.AtualizaListaLojasEvent;
import pitstop.com.br.pitstop.event.AtualizaListaProdutoEvent;
import pitstop.com.br.pitstop.model.Loja;
import pitstop.com.br.pitstop.model.Usuario;
import pitstop.com.br.pitstop.preferences.UsuarioPreferences;
import pitstop.com.br.pitstop.sic.ObjetosSinkSincronizador;


import android.content.res.Configuration;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

public class NavigationViewActivity extends AppCompatActivity {

    private static DrawerLayout mDrawerLayout;
    private static ActionBarDrawerToggle mDrawerToggle;
    private static Toolbar toolbar;
    private static FragmentManager fragmentManager;
    private static NavigationView navigationView;
    private ProgressDialog progressDialog;
    ObjetosSinkSincronizador objetosSinkSincronizador;
    EventBus bus = EventBus.getDefault();
    ImageButton botaoLogout;


    Loja lojaVindaDaTelaDeListarProduto;
    Usuario usuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_view);


        objetosSinkSincronizador = new ObjetosSinkSincronizador(this);

        Intent intent = getIntent();
        lojaVindaDaTelaDeListarProduto = (Loja) intent.getSerializableExtra("loja");

        initViews();
        setUpHeaderView();
        onMenuItemSelected();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        //Esse trecho de codigo é para colocar notificação no navigation drawer
    /*    TextView aniversariantesAlert = new TextView(this);
        aniversariantesAlert.setTextColor(Color.WHITE);
        aniversariantesAlert.setGravity(Gravity.CENTER);
        aniversariantesAlert.setBackgroundResource(R.drawable.custom_circle_orange);
        aniversariantesAlert.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.LEFT | Gravity.CENTER_VERTICAL));
        aniversariantesAlert.setText(String.valueOf(5));

        navigationView.getMenu().findItem(R.id.dashboard).setActionView(aniversariantesAlert);*/

        objetosSinkSincronizador.buscaTodos();

        if (lojaVindaDaTelaDeListarProduto != null) {
            navigationView.setCheckedItem(R.id.produto);
            MenuItem item = navigationView.getMenu().findItem(R.id.produto);
            Fragment fragment = new ListarProdutoFragment();
            setFragment(fragment, item);
            //intent.removeExtra("loja");

        } else {
            //At start set home fragment
            if (savedInstanceState == null) {

                navigationView.setCheckedItem(R.id.dashboard);
                MenuItem item = navigationView.getMenu().findItem(R.id.dashboard);
                Fragment fragment = new DashboardFragment();
                setFragment(fragment, item);
            }
        }

        botaoLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                progressDialog.setMessage("Deslogando usuario");
                progressDialog.show();
//                logout("usuarioLogado-" + usuario.getNome());
                UsuarioPreferences usuarioPreferences = new UsuarioPreferences(getApplicationContext());
                usuarioPreferences.deletar();
                progressDialog.dismiss();
                Intent intentVaiProLogin = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intentVaiProLogin);
                finish();


            }
        });


    }


    /*  Init all views  */
    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.slider_menu);
        fragmentManager = getSupportFragmentManager();
        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout, toolbar, // nav menu toggle icon
                R.string.drawer_open, // nav drawer open - description for
                // accessibility
                R.string.drawer_close // nav drawer close - description for
                // accessibility
        ) {
            @Override
            public void onDrawerClosed(View view) {
                getMenuInflater();
                super.onDrawerClosed(view);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
                super.onDrawerOpened(drawerView);

            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
//        objetosSinkSincronizador.buscaTodos();
        Log.e("TESTE", "chama o resume");


    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    /**
     * For using header view use this method
     **/
    private void setUpHeaderView() {

        View headerView = navigationView.inflateHeaderView(R.layout.header_view);

        UsuarioPreferences usuarioPreferences = new UsuarioPreferences(this);
        if (usuarioPreferences.temUsuario()) {
            usuario = usuarioPreferences.getUsuario();
        }
        botaoLogout = (ImageButton) headerView.findViewById(R.id.logout);
        TextView textOne = (TextView) headerView.findViewById(R.id.username);
        TextView loja = (TextView) headerView.findViewById(R.id.loja);
        if (usuarioPreferences.temLoja()) {
            loja.setText("Loja: " + usuarioPreferences.getLoja().getNome());
        } else {
            loja.setText("primeiro acesso");
        }
        textOne.setText("Funcionário: " + usuario.getNome());
        TextView textTwo = (TextView) headerView.findViewById(R.id.email_address);
        textTwo.setText("Cargo: " + usuario.getRole());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_sinc, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.sincronizar_dados:
                objetosSinkSincronizador.buscaTodos();
                bus.post(new AtualizaListaProdutoEvent());
                bus.post(new AtualizaListaLojasEvent());


                break;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void destacarItemSelecionado(MenuItem menuItem) {
        for (int i = 0; i < navigationView.getMenu().size(); i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
        }

        menuItem.setChecked(true);
    }

    /*  Method for Navigation View item selection  */
    private void onMenuItemSelected() {

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                final Menu menu = navigationView.getMenu();


                //Check and un-check menu item if they are checkable behaviour
//                if (item.isCheckable()) {
//                    if (item.isChecked()) item.setChecked(false);
//                    else item.setChecked(true);
//                }

                destacarItemSelecionado(item);

                Fragment fragment = null;
                switch (item.getItemId()) {
                    case R.id.dashboard:

                        objetosSinkSincronizador.buscaTodos();
                        fragment = new DashboardFragment();
                        setFragment(fragment, item);
                        break;
//
                    case R.id.loja:

                        objetosSinkSincronizador.buscaTodos();
                        fragment = new ListarLojaFragment();
                        //Replace fragment
                        setFragment(fragment, item);
                        break;

                    case R.id.produto:
                        //Replace fragment

                        objetosSinkSincronizador.buscaTodos();

                        fragment = new ListarProdutoFragment();

                        //Replace fragment


                        setFragment(fragment, item);


//                        setFragment(item);
                        break;
//                    case R.id.cad_vendas:
//                        Intent intentVaiProFormularioVendas = new Intent(getApplicationContext(), CadastroVendaActivity.class);
//                        startActivity(intentVaiProFormularioVendas);
//                        //Replace fragment


//                        setFragment(item);
//                        break;
                    case R.id.entrada_de_produto:
                        objetosSinkSincronizador.buscaTodos();
                        Intent intentVaiProFormulario = new Intent(getApplicationContext(), CadastroEntradaProdutoActivity.class);
                        startActivity(intentVaiProFormulario);
//                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                        //Replace fragment


//                        setFragment(item);

                        break;
                    case R.id.cad_vendas:
                        objetosSinkSincronizador.buscaTodos();
                        Intent intentVaiProCadVendas = new Intent(getApplicationContext(), CadastrarVendasActivity.class);
                        startActivity(intentVaiProCadVendas);
                        break;
                    case R.id.movimentacao_produto:
                        objetosSinkSincronizador.buscaTodos();
                        Intent intentVaiProCadMovProduto = new Intent(getApplicationContext(), CadastroMovimentacaoProdutoActivity.class);
                        startActivity(intentVaiProCadMovProduto);
                        break;
                    case R.id.relatorio_vendas:
                        objetosSinkSincronizador.buscaTodos();
                        Intent intentVaiProRelatVendas = new Intent(getApplicationContext(), RelatorioVendasActivity.class);
                        startActivity(intentVaiProRelatVendas);
                        break;
                    case R.id.relatorio_entrada_produto:
                        objetosSinkSincronizador.buscaTodos();
                        Intent intentVaiPraRelEntradaProdutos = new Intent(getApplicationContext(), RelatorioEntradaProdutoActivity.class);
                        startActivity(intentVaiPraRelEntradaProdutos);
                        break;
                    case R.id.relatorio_movimentacao_produto:
                        objetosSinkSincronizador.buscaTodos();
                        Intent intentVaiProRelMovimProduto = new Intent(getApplicationContext(), RelatorioMovimentacaoProdutoActivity.class);
                        startActivity(intentVaiProRelMovimProduto);
                        break;
                    case R.id.cad_avaria:
                        objetosSinkSincronizador.buscaTodos();
                        Intent intentVaiProCadAvaria = new Intent(getApplicationContext(), CadastroAvariaActivity.class);
                        startActivity(intentVaiProCadAvaria);
                        break;
                    case R.id.relatorio_avaria:
                        objetosSinkSincronizador.buscaTodos();
                        Intent intentVaiProRelatAvaria = new Intent(getApplicationContext(), RelatorioAvariaActivity.class);
                        startActivity(intentVaiProRelatAvaria);
                        break;
                    case R.id.cadastrar_usuario:
                        objetosSinkSincronizador.buscaTodos();
                        fragment = new ListarUsuarioFragment();
                        setFragment(fragment, item);


//                        Intent intentVaiProCadUsuario = new Intent(getApplicationContext(), SignupActivity.class);
//                        startActivity(intentVaiProCadUsuario);
                        break;
                    case R.id.cadastrar_furo:
                        objetosSinkSincronizador.buscaTodos();
                        Intent intentVaiProCadFfuro = new Intent(getApplicationContext(), CadastroFuroActivity.class);
                        startActivity(intentVaiProCadFfuro);
                        break;
                    case R.id.relatorio_furo:
                        objetosSinkSincronizador.buscaTodos();
                        Intent intentVaiProRelfuro = new Intent(getApplicationContext(), RelatorioFuroActivity.class);
                        startActivity(intentVaiProRelfuro);
                        break;
                    case R.id.relatorio_geral:
                        objetosSinkSincronizador.buscaTodos();
                        Intent intentVaiProRelGeral = new Intent(getApplicationContext(), RelatorioGeralActivity.class);
                        startActivity(intentVaiProRelGeral);
                        break;


//                    case R.id.share_app:
//
//                        //Start new Activity or do your stuff
//
//
//                        Toast.makeText(NavigationViewActivity.this, "You Clicked on " + " + item.getTitle().toString() + " + " menu item.", Toast.LENGTH_SHORT).show();
//                        break;
//                    case R.id.rate_app:
//                        //Start new Activity or do your stuff
//
//
//                        Toast.makeText(NavigationViewActivity.this, "You Clicked on " + "  item.getTitle().toString()  " + " menu item.", Toast.LENGTH_SHORT).show();
//                        break;
//                    case R.id.settings:
//                        //Start new Activity or do your stuff
//
//
//                        Toast.makeText(NavigationViewActivity.this, "You Clicked on " + " + item.getTitle().toString() + " + " menu item.", Toast.LENGTH_SHORT).show();
//                        break;
//                    case R.id.help:
//                        //Start new Activity or do your stuff
//
//
//                        Toast.makeText(NavigationViewActivity.this, "You Clicked on " + " + item.getTitle().toString() + " + " menu item.", Toast.LENGTH_SHORT).show();
//
//                        break;

                }
                //Closing drawer on item click
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }


    /*  Set Fragment, setting toolbar title and passing item title via bundle to fragments*/
    public void setFragment(Fragment fragment, MenuItem item) {
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(item.getTitle().toString());
        }


    }


    //On back press check if drawer is open and closed
    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT))
            mDrawerLayout.closeDrawers();
        else
            super.onBackPressed();
    }
}