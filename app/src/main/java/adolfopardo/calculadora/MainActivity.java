package adolfopardo.calculadora;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.EditTextInput)
    EditText EditTextInput;
    @BindView(R.id.activity_main)
    RelativeLayout activityMain;


    private boolean onIsEditInProgress=false;
    private int minLenght;
    private int textSize;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        minLenght = getResources().getInteger(R.integer.main_min_length);
        textSize = getResources().getInteger(R.integer.main_input_textSize);

        configEditText();

    }

    //METEDO OCULTAR EL TECLADO ANDROID CUANDO SE SELECCIONA EL EDITTEXT
    private void configEditText() {
//        EditTextInput.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                InputMethodManager input = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                input.hideSoftInputFromWindow(view.getWindowToken(), 0);
//            }
//        });

        //ELIMINAR CARACTERES CON EL DRAWABLE
        EditTextInput.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction()==MotionEvent.ACTION_UP){
                    if (motionEvent.getRawX() >= (EditTextInput.getRight() -
                            EditTextInput.getCompoundDrawables()[Constantes.DRAWABLE_RIGHT].getBounds().width())){
                        if (EditTextInput.length()>0){
                            final int lenght = EditTextInput.getText().length();
                            EditTextInput.getText().delete(lenght-1,lenght);
                        }

                    }
                    return true;
                }
                return false;
            }
        });
        //METODO PARA CAMBIAR EL OPERADOR ANTERIOR
        EditTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!onIsEditInProgress &&
                        Metodos.canReplaceOperator(charSequence)){
                    onIsEditInProgress=true;
                    EditTextInput.getText().delete(EditTextInput.getText().length()-2,
                            EditTextInput.getText().length()-1);
                }
                    //AJUSTAR TAMAÑO DE TEXTO SEGUN pantalla
                    if (charSequence.length() > minLenght){
                        EditTextInput.setTextSize(TypedValue.COMPLEX_UNIT_SP,textSize-
                                (((charSequence.length() - minLenght) * 2)+ (charSequence.length()-
                        minLenght)));
                    }
                    else
                        EditTextInput.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);

            }

            @Override
            public void afterTextChanged(Editable editable) {
                onIsEditInProgress = false;
            }
        });
    }


    @OnClick({R.id.btnSeven, R.id.btnFour, R.id.btnOne, R.id.btnEight, R.id.btnfive, R.id.btnTwo,
            R.id.btnNine, R.id.btnSix, R.id.btnThree, R.id.btnPoint, R.id.btnZero})
    public void onClickNumbers(View view) {

        final String valStr = ((Button) view).getText().toString();

        switch (view.getId()) {
            case R.id.btnZero:
            case R.id.btnOne:
            case R.id.btnTwo:
            case R.id.btnThree:
            case R.id.btnFour:
            case R.id.btnfive:
            case R.id.btnSix:
            case R.id.btnSeven:
            case R.id.btnEight:
            case R.id.btnNine:
                EditTextInput.getText().append(valStr);
                break;
            case R.id.btnPoint:
                final String opeacion = EditTextInput.getText().toString();

                final String operador = Metodos.getOperator(opeacion);

                final int count = opeacion.length() - opeacion.replace(".", "").length();

                if (!opeacion.contains(Constantes.POINT) ||
                        (count < 2 && (!operador.equals(Constantes.OPERATOR_NULL)))) {
                    EditTextInput.getText().append(valStr);
                }
                break;

        }
    }

    @OnClick({R.id.btnClear, R.id.btnDiv, R.id.btnMultiplication, R.id.btnSub, R.id.btnSum, R.id.btnResult})
    public void onClickControls(View view) {
        switch (view.getId()) {
            case R.id.btnClear:
                EditTextInput.setText("");
                break;
            case R.id.btnDiv:
            case R.id.btnMultiplication:
            case R.id.btnSub:
            case R.id.btnSum:
                resolve(false);

                final String operador = ((Button)view).getText().toString();
                final String operacion = EditTextInput.getText().toString();

                final String ultimoCaracter = operacion.isEmpty()? "" :
                        operacion.substring(operacion.length()-1);

                if (operador.equals(Constantes.OPERATOR_SUB)) {
                    if (operacion.isEmpty() ||
                            (!(ultimoCaracter.equals(Constantes.OPERATOR_SUB))
                                    && !(ultimoCaracter.equals(Constantes.POINT)))) {

                        EditTextInput.getText().append(operador);
                        }
                    } else {
                        if (!operacion.isEmpty() &&
                                !(ultimoCaracter.equals(Constantes.OPERATOR_SUB)) &&
                                !(ultimoCaracter.equals(Constantes.POINT))) {
                            EditTextInput.getText().append(operador);
                        }
                    }
                break;
            case R.id.btnResult:
                resolve(true);
                break;
        }
    }
    private void resolve(boolean fromResult){
        Metodos.tryResolve(fromResult, EditTextInput, new OnResolveCallback() {
            @Override
            public void onShowMessage(int errorRes) {
                showMessage(errorRes);
            }

            @Override
            public void onIsEditing() {
                onIsEditInProgress=true;
            }
        });
    }

    private void showMessage(int errorRes) {
        Snackbar.make(activityMain, errorRes,Snackbar.LENGTH_SHORT).show();
    }
}

