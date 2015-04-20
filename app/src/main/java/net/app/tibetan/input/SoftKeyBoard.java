package net.app.tibetan.input;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Toast;

import com.example.like.tibetan.R;
import com.example.like.tibetan.TiBenView;

import net.app.tibetan.view.CandidateView;
import net.app.tibetan.view.CandidatesContainer;
import net.app.tibetan.view.DrawView;
import net.app.tibetan.view.LatinKeyboard;
import net.app.tibetan.view.LatinKeyboardView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by like on 15-4-18.
 */
public class SoftKeyBoard  extends InputMethodService implements
        KeyboardView.OnKeyboardActionListener{
    private LatinKeyboardView mInputView;
    private LatinKeyboard QwertKeyboard;
    private LatinKeyboard mWeiYuKeyboard;
    private LatinKeyboard mSymbolsKeyboard;
    private LatinKeyboard mTibetanKeyboard;
    private LatinKeyboard mWriteKeyBoard;
    private LatinKeyboard mCurKeyboard;
    private TiBenView tibenView;
    private LatinKeyboardView latinKeyboardView;
    CandidatesContainer candidateContainer;
    private CandidateView candidateView;
    private StringBuilder mComposing = new StringBuilder();
    private CompletionInfo[] mCompletions;
    private CandidatesContainer candidatesContainer;
    private String mWordSeparators;
    public static Handler Writehandler;
    public static final int CANDIDATE_VIEW_MEAT=1;
    private boolean mCapsLock;
    private DrawView drawView;
    //Ÿö¶šÄÜ²»ÄÜÓÐºòÑ¡Àž
    private boolean mPredictionOn=false;
    private StringBuilder result=new StringBuilder();
    private StringBuilder result2;
    private String result3;
    private final static int CWJ_HEAP_SIZE=6*1024*1024;
    public static Handler writeHandler;
    public static boolean MyTibe=false;
    public void onCreate() {
        super.onCreate();
        mWordSeparators = getResources().getString(R.string.word_separators);
        Writehandler=new Handler(){
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 98:
                        int a[]=(int[])msg.obj;
                        format(a);
                        drawView.clearnDraw();
                        Toast.makeText(SoftKeyBoard.this, "!!!!!!!!!!!", Toast.LENGTH_LONG).show();
                        mComposing=result;
                        if(result.length()>0){
                            getCurrentInputConnection().setComposingText(mComposing,mComposing.length());
                            updateCandidates();
                            candidateView.myWriteFlag=true;
                        }

                }
            }
        };
    }
    public void format(int[] a){
        int j=0;
        while(a[j]!=0){
            result.append(a[j]);
            j++;
        }
    }
    public void onInitializeInterface(){
        QwertKeyboard=new LatinKeyboard(this, R.xml.qwerty);
        mTibetanKeyboard=new LatinKeyboard(this,R.xml.tibetan);
        mWriteKeyBoard=new LatinKeyboard(this,R.xml.write);
    }
    @Override
    public View onCreateCandidatesView(){
        candidateContainer=(CandidatesContainer)getLayoutInflater().inflate(R.layout.candidates, null);
        candidateContainer.initialize();
        candidateContainer.setService(this);
        candidateView=(CandidateView)candidateContainer.findViewById(R.id.candidate_view1);
        candidateView.setService(this);
        setCandidatesViewShown(true);
        return candidateContainer;
    }
    @Override
    public View onCreateInputView(){
        View view=getLayoutInflater().inflate(R.layout.input_view, null);
        mInputView=(LatinKeyboardView)view.findViewById(R.id.add_keyboard_view);
        mInputView.setOnKeyboardActionListener(this);
        mInputView.setKeyboard(QwertKeyboard);
        drawView=(DrawView)view.findViewById(R.id.draw);
        tibenView=(TiBenView)view.findViewById(R.id.select_word_view);
        return view;
    }
    /**
     * Helper function to commit any text being composed in to the editor.
     */
    private void commitTyped(StringBuilder content) {
        InputConnection ic =getCurrentInputConnection();
        content=mComposing;
        if(ic!=null){
            if (content.length() > 1) {
                ic.beginBatchEdit();
                updateCandidates();
                ic.commitText(content, mComposing.length());
                ic.endBatchEdit();
            }else{
                ic.commitText(content, mComposing.length());
                updateCandidates();

            }
        }
    }
    public void setLatinKeyboardView(LatinKeyboard keyboard){
        mInputView.setKeyboard(keyboard);
    }
    public void hideKeyboard()
    {
        requestHideSelf(0);
    }
    //ŽŠÀí¹â±êÒÆ¶¯
    @Override
    public void onUpdateSelection(int oldSelStart, int oldSelEnd,
                                  int newSelStart, int newSelEnd,
                                  int candidatesStart, int candidatesEnd) {
        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd,
                candidatesStart, candidatesEnd);
        if (mComposing.length() > 0 && (newSelStart != candidatesEnd
                || newSelEnd != candidatesEnd)) {
	       	/*mComposing.setLength(0);
	       	//ºòÑ¡ÀžÖÃ¿Õ
	           updateCandidates();*/
            //ÕâžöÓïŸäºÍÏÂÃæµÄifÀïÃæµÄÄÇžöŸö¶šÁËœáÊøÊäÈëµÄÈ«¹ý³Ì
            InputConnection ic = getCurrentInputConnection();
            if (ic != null) {
                //ÕâžöÓïŸäµÄ×÷ÓÃÊÇ£¬ÈÃÊäÈëÄ¿±êÄÚµÄÏÂ»®ÏßÈ¥µô£¬Íê³ÉÒ»ŽÎ±àŒ­
                  ic.finishComposingText();

            }
        }
    }
    /**
     * Update the list of available candidates from the current composing
     * text.  This will need to be filled in by however you are determining
     * candidates.
     */
    private void updateCandidates() {
        if (mComposing.length() >0) {
            ArrayList<String> list = new ArrayList<String>();
            list.add(mComposing.toString());
            setSuggestions(list, true, true);

        } else {
            setSuggestions(null, false, false);
        }
    }
    public void setSuggestions(List<String> suggestions, boolean completions,
                               boolean typedWordValid) {
        candidateContainer.showCandidates(suggestions);
    }
    /**
     * Helper to update the shift state of our keyboard based on the initial
     * editor state.
     */
    private void updateShiftKeyState(EditorInfo attr) {
        if (attr != null
                && mInputView != null && QwertKeyboard == mInputView.getKeyboard()) {
            int caps = 0;
            EditorInfo ei = getCurrentInputEditorInfo();
            if (ei != null && ei.inputType != InputType.TYPE_NULL) {
                caps = getCurrentInputConnection().getCursorCapsMode(attr.inputType);
            }
            mInputView.setShifted(mCapsLock || caps != 0);
        }
    }

    private boolean isAlphabet(int code){
        if(Character.isLetter(code)){
            return true;
        }else{
            return false;
        }
    }
    /*
     * Helper to send a key down / key up pair to the current editor.
     **/
    private void keyDownUp(int keyEventCode){
        //Õâžöº¯ÊýÊÇÓÃÀŽœøÐÐÌØÊâÊä³öµÄ£¬ŸÍºÃÏñÇ°Ãæ¶šÒåµÄ"android"Êä³ö£¬µ«Èç¹ûŽÓŒüÅÌÉÏÊäÈë×Ö·û£¬ÊÇ²»»áŸ­¹ýÕâÒ»²œ
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_DOWN,keyEventCode)
        );
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_UP,keyEventCode
                ));
    }

    /*
     *Helper to send a character to the editor as raw key events
     * */
    private void sendKey(int keyCode){
        //ŽŠÀíÖÐ¶Ï·û
        switch(keyCode){
		    case '\n':
	          keyDownUp(KeyEvent.KEYCODE_ENTER);
	          break;
            default:
                if(keyCode>='0'&&keyCode<='9'){
                    keyDownUp(keyCode-'0'+KeyEvent.KEYCODE_0);
                }else{
                    commitTyped(mComposing);
                    getCurrentInputConnection().commitText(String.valueOf((char)keyCode),mComposing.length());
                    mComposing.setLength(0);
                    updateCandidates();
                }
                break;
        }
    }
    private String getWordSeparators(){
        return mWordSeparators;
    }

    public boolean isWordSeparator(int code){
        String separators=getWordSeparators();
        return separators.contains(String.valueOf((char)code));
    }
    private void handleBackspace(){

        final int length=mComposing.length();
        if(length>1){
            mComposing.delete(length-1, length);
        }else if(length>0){

            mComposing.setLength(0);
        }else{
            keyDownUp(KeyEvent.KEYCODE_DEL);
        }
        getCurrentInputConnection().setComposingText(mComposing, mComposing.length());
        updateCandidates();
    }
    private void handleShift(){
        if(mInputView==null){
            return;
        }
        Keyboard currenKeyboard=mInputView.getKeyboard();
        if(currenKeyboard==currenKeyboard){
            checkToggleCapsLock();
            mInputView.setShifted(mCapsLock||!mInputView.isShifted());
        }
    }
    private void handleLanguageSwitch(){
        if(mInputView==null){
            return;
        }
    }
    private void checkToggleCapsLock(){

        long now=System.currentTimeMillis();
    }
    private void handleCharacter(int primaryCode,int[] keyCodes){
        if(isAlphabet(primaryCode)){
            mComposing.append((char)primaryCode);
            getCurrentInputConnection().setComposingText(mComposing,1);
            updateCandidates();
        }else{
            getCurrentInputConnection().commitText(mComposing, 1);
        }
    }
    private void handleClose(){
        requestHideSelf(0);
        mInputView.closing();
    }
    public void onKey(int primaryCode, int[] keyCodes) {

        if (isWordSeparator(primaryCode)) {

            sendKey(primaryCode);
            updateShiftKeyState(getCurrentInputEditorInfo());
        } else if (primaryCode == Keyboard.KEYCODE_DELETE) {

            handleBackspace();
        } else if (primaryCode == Keyboard.KEYCODE_SHIFT) {

            setLatinKeyboardView(QwertKeyboard);

        }else if(primaryCode ==-15){

            setLatinKeyboardView(mWeiYuKeyboard);
        }
        else if (primaryCode == Keyboard.KEYCODE_CANCEL) {

            handleClose();
            return;
        } else if (primaryCode == LatinKeyboardView.KEYCODE_LANGUAGE_SWITCH) {

            handleLanguageSwitch();
            return;
        } else if (primaryCode == LatinKeyboardView.KEYCODE_OPTIONS) {

        }else if(primaryCode ==-112 && mInputView != null){

            setLatinKeyboardView(mSymbolsKeyboard);
        } else if(primaryCode==-7){
            if(mCapsLock){
                mInputView.setShifted(true);
            }
            handleShift();
        }
        else {

            handleCharacter(primaryCode, keyCodes);
        }
    }
    @Override
    public void onPress(int arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onRelease(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onText(CharSequence arg0) {
        // TODO Auto-generated method stub

    }
    @Override
    public void swipeDown() {
        // TODO Auto-generated method stub

    }

    @Override
    public void swipeLeft() {
        // TODO Auto-generated method stub

    }

    @Override
    public void swipeRight() {
        // TODO Auto-generated method stub

    }

    @Override
    public void swipeUp() {
        // TODO Auto-generated method stub

    }

    @Override public void onDisplayCompletions(CompletionInfo[] completions) {
        //µ±ÐèÒªÔÚºòÑ¡ÀžÀïÃæÏÔÊŸautoµÄÄÚÈÝ
        //ŽËº¯Êý×÷ÓÃ£¬²Â²â£ºµ±È«ÆÁÄ»Ä£ÊœµÄÊ±ºò£¬mCompletionOnÖÃtrue,¿ÉÒÔÍš¹ýºòÑ¡ÀžÀŽÏÔÊŸauto
        Log.i("mytest", "SoftKeyboard_onDisplayCompletions");
        //±ØÐëÕâžö±äÁ¿ÔÊÐí
        mCompletions = completions; //ž³Öµžø±ŸÀŽÀïÃæ×šÃÅŒÇÂŒºòÑ¡ÖµµÄ±äÁ¿
        if (completions == null) {
            setSuggestions(null, false, false); //Èç¹ûÃ»ÓÐºòÑ¡ŽÊ£¬ŸÍÕâÑùŽŠÖÃ
            return;
        }

        List<String> stringList = new ArrayList<String>();
        for (int i=0; i<(completions != null ? completions.length : 0); i++) {
            CompletionInfo ci = completions[i];
            if (ci != null) stringList.add(ci.getText().toString());
        }
        setSuggestions(stringList, true, true);

    }
    public void pickSuggestionManually(int index) {
        Log.i("mytest", "SoftKeyboard_pickSuggestionManually");
        if ( mCompletions != null && index >= 0
                && index < mCompletions.length) {
            CompletionInfo ci = mCompletions[index];
            getCurrentInputConnection().commitCompletion(ci);
            if (candidateView != null) {
                candidateView.clear();
            }
            updateShiftKeyState(getCurrentInputEditorInfo());
        } else if (mComposing.length() > 0) {
            // If we were generating candidate suggestions for the current
            // text, we would commit one of them here.  But for this sample,
            // we will just commit the current text.
            commitTyped(mComposing);
            mComposing.setLength(0);
            candidateView.clear();
            updateCandidates();
        }
    }
    public void changeKeyboard(){
        if(mInputView.getKeyboard()==QwertKeyboard){
            setLatinKeyboardView(mTibetanKeyboard);
            drawView.setVisibility(View.VISIBLE);
        }else if(mInputView.getKeyboard()==mTibetanKeyboard){
            setLatinKeyboardView(QwertKeyboard);
            drawView.setVisibility(View.GONE);
        }else if(mInputView.getKeyboard()==mWriteKeyBoard){
            setLatinKeyboardView(QwertKeyboard);
            tibenView.setVisibility(View.GONE);

        }
    }
    public void changeWriteTibe(){
       if(mInputView.getKeyboard()==mTibetanKeyboard){
            setLatinKeyboardView(mWriteKeyBoard);
            drawView.setVisibility(View.GONE);
            tibenView.setVisibility(View.VISIBLE);
        }
    }
}
