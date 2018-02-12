package pitstop.com.br.pitstop.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by wilso on 23/10/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String BANCO_DADOS = "Pitstop";
    private static final int VERSAO = 2;

    public DatabaseHelper(Context context) {
        super(context, "BANCO_DADOS", null, VERSAO);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        //tabela de loja
        String sql = "CREATE TABLE Lojas (id CHAR(36) PRIMARY KEY, nome TEXT NOT NULL, endereco TEXT NOT NULL, sincronizado INT DEFAULT 0 );";
        db.execSQL(sql);

        //tabela produto
        sql = "CREATE TABLE Produtos (id CHAR(36) PRIMARY KEY,id_ProdutoPrincipal CHAR(36) , vinculo INTEGER NOT NULL,  nome TEXT NOT NULL, preco REAL NOT NULL,estoque_minimo INTEGER NOT NULL, quantidade INTEGER NOT NULL, loja_id CHAR(36) NOT NULL, sincronizado INT DEFAULT 0);";
        db.execSQL(sql);

        //tabela EntradaProduto
        sql = "CREATE TABLE EntradaProduto (id CHAR(36) PRIMARY KEY,desativado INT DEFAULT 0, precoDeCompra REAL NOT NULL,data TEXT NOT NULL, quantidade INTEGER NOT NULL,produto_id CHAR(36) NOT NULL, sincronizado INT DEFAULT 0, quantidadeVendidaMovimentada INT DEFAULT 0);";
        db.execSQL(sql);

        //tabela venda
        sql = "CREATE TABLE Vendas (id CHAR(36) PRIMARY KEY,desativado INT DEFAULT 0, total REAL NOT NULL,totalCartao REAL NOT NULL,prejuizo REAL NOT NULL, id_loja CHAR(36) ,nomeVendedor TEXT NOT NULL,formaDePagamento TEXT NOT NULL, dataDaVenda TEXT NOT NULL, sincronizado INT DEFAULT 0);";
        db.execSQL(sql);

        //tabela Venda_EntradaProduto
        sql = "CREATE TABLE Venda_EntradaProduto (id CHAR(36) PRIMARY KEY,id_produto CHAR(36),id_entradaProduto CHAR(36),id_venda CHAR(36),precoDeVenda REAL NOT NULL,quantidadeVendida INTEGER NOT NULL,sincronizado INT DEFAULT 0);";
        db.execSQL(sql);

        //tabela movimentacao_produto
        sql = "CREATE TABLE movimentacao_produto (id CHAR(36) PRIMARY KEY,desativado INT DEFAULT 0,id_lojaDe CHAR(36),id_lojaPara CHAR(36),id_Produto TEXT NOT NULL,data TEXT NOT NULL ,quantidade INTEGER NOT NULL,sincronizado INT DEFAULT 0);";
        db.execSQL(sql);

        //tabela Avaria
        sql = "CREATE TABLE Avaria (id CHAR(36) PRIMARY KEY,desativado INT DEFAULT 0,prejuizo REAL NOT NULL,id_loja CHAR(36),data TEXT NOT NULL,sincronizado INT DEFAULT 0);";
        db.execSQL(sql);

        //tabela Avaria_EntradaProduto
        sql = "CREATE TABLE Avaria_EntradaProduto (id CHAR(36) PRIMARY KEY,id_entradaProduto CHAR(36),id_avaria CHAR(36),quantidade INTEGER NOT NULL,sincronizado INT DEFAULT 0);";
        db.execSQL(sql);


        //tabela Furo
        sql = "CREATE TABLE Furo (id CHAR(36) PRIMARY KEY,desativado INT DEFAULT 0,valor REAL NOT NULL,id_loja CHAR(36),id_usuario CHAR(36),data TEXT NOT NULL,sincronizado INT DEFAULT 0);";
        db.execSQL(sql);

        //tabela Furo_EntradaProduto
        sql = "CREATE TABLE Furo_EntradaProduto (id CHAR(36) PRIMARY KEY,id_entradaProduto CHAR(36),id_furo CHAR(36),quantidade INTEGER NOT NULL,sincronizado INT DEFAULT 0);";
        db.execSQL(sql);

        //tabela Usuario
        sql = "CREATE TABLE Usuarios (nome TEXT NOT NULL PRIMARY KEY,desativado INT DEFAULT 0,role TEXT NOT NULL,senha TEXT NOT NULL,sincronizado INT DEFAULT 0);";
        db.execSQL(sql);

        //Produtoo Vinculo
        sql = "CREATE TABLE ProdutoVinculo (id CHAR(36) PRIMARY KEY,id_produto CHAR(36));";
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


    }
}
