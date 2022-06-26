package gals;

import compiladoresM2.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Semantico implements Constants
{

    String type;
    String warningOutput = "Compilado com Warning\n";


    public class Simbolo{
        public String name;
        public String type;
        public boolean init = false;
        public boolean used = false;
        public int scope = 0;
        public boolean param = false;
        public int pos = 0;
        public boolean vect=false;
        public boolean matrix = false;
        public boolean ref=  false;
        public boolean func;
        public int size =1;
    }
    private boolean vecExp = false;
    public List<Simbolo> symbolTable = new ArrayList<Simbolo>();
    public List<Integer> pileOfScopes = new ArrayList<Integer>();
    private int scopeCounter=0;
    Simbolo lastSimbol = new Simbolo();
    public boolean isAttr = false;
    private boolean isCin = false;
    private boolean isCout = false;
    private boolean isVect = false;
    private boolean isBranch = false;
    public boolean isDo = false;
    public boolean isFor = false;
    public boolean isFunc = false;
    public String indexOfAttr;
    public List<String> attrPile = new ArrayList<>();
    public List<String> attrPileOfLex = new ArrayList<>();
    public List<String> indexExp = new ArrayList<>();
    public List<Integer> positionInPile = new ArrayList<>();
    public List<IndexHelper> indexList = new ArrayList<>();
    public boolean isWhile= false;
    public boolean isIf= false;
    private int storeCount;
    public SemanticTableHelper helper = new SemanticTableHelper();
    public SemanticTable semanticTable = new SemanticTable();
    Simbolo lastAddSymbol= new Simbolo();
    public  Assembly assembly = new Assembly();
    public Simbolo elementOnTheLeftSideOfAttr;
    boolean leftElementIsVect = false;
    ArrayList<String> coutPile = new ArrayList<>();
    ArrayList<String> coutPileOfTypes = new ArrayList<>();
    ArrayList<String> cinPile = new ArrayList<>();
    ArrayList<String> cinPileOfTypes = new ArrayList<>();
    public ArrayList<String> expPilesOfBranches = new ArrayList<>();
    private String firstElementOfFor;
    int globalLastAction;
    public boolean endElse = false;
    public boolean isParameter = false;
    public String calledFunction;
    public ArrayList<String> calledParameters = new ArrayList<>();


    public String listOfOps[] = {"+","-","<<",">>","|","&"};
    public void printTable(){
        System.out.println("Nome|Tipo|Func|Vet|Inic|Param|Pos|Ref|Escopo|Usado");
        for(Simbolo sim : this.symbolTable){
            System.out.println(sim.name +" " + sim.type + " " + sim.func + " " + sim.vect + " " + sim.init + " " + sim.param +" " + sim.pos + " " + sim.ref + " " + sim.scope + " " + sim.used );
        }
    }

    public Object[][] fillTable(){

        Object[][] data = new Object[this.symbolTable.size()][10];
        int i = 0;
        for(Simbolo sim : this.symbolTable){
            data[i][0] = sim.name;
            data[i][1] = sim.type;
            data[i][2] = sim.func;
            data[i][3] = sim.vect;
            data[i][4] = sim.init;
            data[i][5] = sim.param;
            data[i][6] = sim.pos;
            data[i][7] = sim.ref;
            data[i][8] = sim.scope;
            data[i][9] = sim.used;
            i++;
        }
        return data;
    }
    public String getWarningOutput(){
        return warningOutput;
    }

    private Simbolo findSymbolSameNameAndScope(Simbolo _lastSimbol){
        for(Simbolo sim : symbolTable){
            if(_lastSimbol.name.equals(sim.name) && _lastSimbol.scope == sim.scope){
                _lastSimbol.init = true;
                return sim;
            }
        }
        return null;
    }

    private boolean checkIfVarExistsCurrentContext(Simbolo sim){
        for (Simbolo simbols : symbolTable){
            if(sim.scope == simbols.scope && sim.name.equals(simbols.name)){
                return true;
            }
        }
        return false;
    }
    private boolean checkIfVarExists(Simbolo sim){
        for (Simbolo simbols : symbolTable){
            if(sim.name.equals(simbols.name)){
                return true;
            }
        }
        return false;
    }
    private boolean checkIfIsInit(Simbolo sim){
        for (Simbolo simbols : symbolTable){
            if(sim.scope == simbols.scope && sim.name.equals(simbols.name)){
                if(sim.init==false){
                    return false;
                }
                else{
                    return true;
                }
            }
        }
        return false;
    }
    private String returnObjType(String name){
        for(Simbolo x : symbolTable){
            if(x.name.equals(name)){
                return x.type;
            }
        }
        return null;
    }

    public void clearTable(){

        this.symbolTable.clear();
        attrPile.clear();
        attrPileOfLex.clear();
        indexExp.clear();
        indexList.clear();
        warningOutput = "";
        pileOfScopes.clear();
        coutPile.clear();
        cinPile.clear();
        cinPileOfTypes.clear();
        expPilesOfBranches.clear();
        Main.branch.branchCounter=0;
        Main.branch.branchPile.clear();
        Main.func.listOfFunctions.clear();
        Main.sem.calledParameters.clear();
        isWhile = false;
    }
    public void changedToUsed(Simbolo sim){
        for(Simbolo simbolo: symbolTable){
            if(sim.scope==simbolo.scope && sim.name.equals(simbolo.name)){
                simbolo.used = true;
            }
        }
    }
    public void changedToInit(String name ){
        for(int i=symbolTable.size()-1;i>=0;i--){
            if(name.equals(symbolTable.get(i).name)){
                symbolTable.get(i).init = true;

            }
        }
    }
    public Simbolo getSimbol(String name, int scope ){
        for(int i=symbolTable.size()-1;i>=0;i--){
            if(name.equals(symbolTable.get(i).name) && symbolTable.get(i).scope==scope){

                return symbolTable.get(i);

            }
        }
        return null;
    }
    private void changeSize(int size, int scope,String  name){
        for(int i=symbolTable.size()-1;i>=0;i--){
            if(name.equals(symbolTable.get(i).name) && scope==symbolTable.get(i).scope){
                symbolTable.get(i).size = size;
            }
        }
    }

    public static boolean checkIfElementIsVect(List<Simbolo> symbolTable, String toCheckValue)
    {
        for (Simbolo x : symbolTable) {
            if (x.name.equals(toCheckValue)) {
                if(x.vect==true){
                    return  true;
                }
            }
        }
        return  false;
    }



    private void checkAttrCase1() throws SemanticError {
        //check if compatible
        int elem = helper.changeTypeStringToInt(attrPile.get(0));
        if(semanticTable.atribType(helper.changeTypeStringToInt(elementOnTheLeftSideOfAttr.type),elem)!=0){
            throw new SemanticError("Erro, tipos diferentes - CASE 1");
        }
        if(leftElementIsVect){
            assembly.simpleVectReceivesVarOrInt(attrPileOfLex.get(0), indexOfAttr, assembly, elementOnTheLeftSideOfAttr.name);
        }else{
            assembly.simpleVarReceivesIntOrVar(attrPileOfLex.get(0),assembly);
        }
    }

    private void checkAttrCase2() throws SemanticError {
        int elem = helper.changeTypeStringToInt(attrPile.get(0));
        if(semanticTable.atribType(helper.changeTypeStringToInt(elementOnTheLeftSideOfAttr.type),elem)!=0){
            throw new SemanticError("Erro, tipos diferentes - CASE 2");
        }
        if(leftElementIsVect){
            assembly.simpleVecReceivesVec(attrPileOfLex.get(0),indexOfAttr,assembly,elementOnTheLeftSideOfAttr.name,attrPileOfLex.get(1));
        }
        else{
            assembly.simpleVarReceivesVect(attrPileOfLex.get(0),assembly,elementOnTheLeftSideOfAttr.name,attrPileOfLex.get(1));
        }
    }

    private int getFirst3_Ops_Index() throws SemanticError {
        int firstEle = helper.changeTypeStringToInt(attrPile.get(1));
        int ops = helper.changeOpsToInt(attrPile.get(2));
        int secEle = helper.changeTypeStringToInt(attrPile.get(3));
        int result = semanticTable.resultType(firstEle,secEle,ops);
        if(result!=0){
            throw new SemanticError("Erro, tipos diferentes - CASE GETFIRST3");
        }
        return result;
    }
    private  void get2by2_Ops(int lastResult,int startingPoint) throws SemanticError {
        for(int i=startingPoint;i<attrPile.size();i=i+2){
            int ops = helper.changeOpsToInt(attrPile.get(i));
            int secEle = helper.changeTypeStringToInt(attrPile.get(i+1));
            lastResult = semanticTable.resultType(lastResult,secEle,ops);
            if(lastResult!=0){
                throw new SemanticError("Erro, tipos diferentes - CASE GET2-2");
            }
            assembly.addToIndex(attrPileOfLex.get(i+1),attrPileOfLex.get(i),assembly);
        }
    }
    private  void get2by2_Ops(int lastResult,int startingPoint,List<Integer>list) throws SemanticError {
        for(int i=startingPoint;i<list.size();i++){
            int ops;
            int secEle;
            //in case its first
            if(list.get(i)==0){
                ops=0;
            }
            else{
                ops = helper.changeOpsToInt(attrPile.get(list.get(i)-1));
            }
            secEle = helper.changeTypeStringToInt(attrPile.get(list.get(i)));
            lastResult = semanticTable.resultType(lastResult,secEle,ops);
            if(lastResult!=0){
                throw new SemanticError("Erro, tipos diferentes - CASE GET-2-2");
            }
            if(list.get(i)==0){
                assembly.addToIndex(attrPileOfLex.get(list.get(i)), "+", assembly);

            }else {
                assembly.addToIndex(attrPileOfLex.get(list.get(i)), attrPileOfLex.get(list.get(i)-1), assembly);
            }
        }
    }

    private int getFirst3_Ops(String first,String op,String sec) throws SemanticError {
        int firstEle = helper.changeTypeStringToInt(first);
        int ops = helper.changeOpsToInt(op);
        int secEle = helper.changeTypeStringToInt(sec);
        int result = semanticTable.resultType(firstEle,secEle,ops);
        if(result!=0){
            throw new SemanticError("Erro, tipos diferentes - CASE GETFIRST3");
        }
        return result;
    }
    public int checkNumberOfVecsInPile(List<String> pile){
        int count = 0;
        for(String index : pile){
            if(checkIfElementIsVect(symbolTable,index)){
                count++;
            }
        }
        return count;
    }
    private void operateWithIndexes() throws SemanticError {
        if(indexList.size()==1){
            int position = indexList.get(0).positionInAttrPile;
            if(semanticTable.atribType(0,helper.changeTypeStringToInt(attrPile.get(position))) !=0){
                throw new SemanticError("Tipo da variavel se difere do esperado");
            }
            assembly.oneIndexStart(assembly,attrPileOfLex.get(indexList.get(0).positionInAttrPile));

        }
        else{
            int position = indexList.get(0).positionInAttrPile;
            int position2 = indexList.get(2).positionInAttrPile;
            int positionOps = indexList.get(1).positionInAttrPile;
            int result = semanticTable.resultType(helper.changeTypeStringToInt(attrPile.get(position)),helper.changeTypeStringToInt(attrPile.get(position2)),helper.changeOpsToInt(attrPile.get(positionOps)));
            if(result!=0){
                throw new SemanticError("Tipo da variavel se difere do esperado");
            }
            assembly.oneIndexStart(assembly,attrPileOfLex.get(position));
            assembly.addToIndex(attrPileOfLex.get(position2),attrPileOfLex.get(positionOps),assembly);
            for(int i=3;i<indexList.size();i=i+2){
                 position = indexList.get(i+1).positionInAttrPile;
                 positionOps = indexList.get(i).positionInAttrPile;
                 result = semanticTable.resultType(result, helper.changeTypeStringToInt(attrPile.get(position)),helper.changeOpsToInt(attrPile.get(positionOps)));
                 if(result!=0){
                     throw new SemanticError("Tipo da variavel se difere do esperado");
                 }
                assembly.addToIndex(attrPileOfLex.get(position),attrPileOfLex.get(positionOps),assembly);
            }
        }

        assembly.endIndex(attrPileOfLex.get((indexList.get(0).positionInAttrPile)-1),assembly);
    }
    private void operateWithIndexesMultiVecForLeftSideArr(int initialPointOfIndexList, int finalPointOfIndexList, int storeCount, int numberOfVecs) throws SemanticError {
        if(indexList.size()==1){
            int position = indexList.get(0).positionInAttrPile;
            if(semanticTable.atribType(0,helper.changeTypeStringToInt(attrPile.get(position))) !=0){
                throw new SemanticError("Tipo da variavel se difere do esperado");
            }
            assembly.oneIndexStart(assembly,attrPileOfLex.get(indexList.get(0).positionInAttrPile));

        }
        else{
            int position;
            int position2;
            int positionOps;
            int result;
            if(initialPointOfIndexList!=finalPointOfIndexList) {
                position = initialPointOfIndexList;
                position2 = initialPointOfIndexList + 2;
                positionOps = initialPointOfIndexList + 1;
                result = semanticTable.resultType(helper.changeTypeStringToInt(attrPile.get(position)), helper.changeTypeStringToInt(attrPile.get(position2)), helper.changeOpsToInt(attrPile.get(positionOps)));
                if (result != 0) {
                    throw new SemanticError("Tipo da variavel se difere do esperado");
                }
                assembly.oneIndexStart(assembly, attrPileOfLex.get(position));
                assembly.addToIndex(attrPileOfLex.get(position2), attrPileOfLex.get(positionOps), assembly);
            }
            //in case one element
            else{
                position = initialPointOfIndexList;
                result = semanticTable.atribType(0,helper.changeTypeStringToInt(attrPile.get(position)));
                assembly.oneIndexStart(assembly, attrPileOfLex.get(position));
            }
            for(int i=initialPointOfIndexList+3;i<finalPointOfIndexList;i++){
                position = initialPointOfIndexList + i;
                positionOps = initialPointOfIndexList + i + 1;
                result = semanticTable.resultType(result, helper.changeTypeStringToInt(attrPile.get(position)),helper.changeOpsToInt(attrPile.get(positionOps)));
                if(result!=0){
                    throw new SemanticError("Tipo da variavel se difere do esperado");
                }
                assembly.addToIndex(attrPileOfLex.get(position),attrPileOfLex.get(positionOps),assembly);
            }
        }
        assembly.endIndex(attrPileOfLex.get(initialPointOfIndexList-1),assembly);
        assembly.storeMultVec(assembly,storeCount);
        boolean isTheLastOne=false;
        if(storeCount>0){
            String negativeOrPositive = attrPileOfLex.get(initialPointOfIndexList-2);
            if(storeCount+1==numberOfVecs){
                isTheLastOne = true;
            }
            assembly.sumOrRedVecsLeftVec(assembly,negativeOrPositive,isTheLastOne);
        }
        if(isTheLastOne){
            treatingExp();
            assembly.endIndexLeftVec(assembly);

        }
    }
    private void operateWithIndexeCout() throws SemanticError {
        if(indexList.size()==1){
            int position = indexList.get(0).positionInCoutPile;
            if(semanticTable.atribType(0,helper.changeTypeStringToInt(coutPileOfTypes.get(position))) !=0){
                throw new SemanticError("Tipo da variavel se difere do esperado");
            }
            assembly.oneIndexStart(assembly,coutPile.get(indexList.get(0).positionInCoutPile));

        }
        else{
            int position = indexList.get(0).positionInCoutPile;
            int position2 = indexList.get(2).positionInCoutPile;
            int positionOps = indexList.get(1).positionInCoutPile;
            int result = semanticTable.resultType(helper.changeTypeStringToInt(coutPileOfTypes.get(position)),helper.changeTypeStringToInt(coutPileOfTypes.get(position2)),helper.changeOpsToInt(coutPileOfTypes.get(positionOps)));
            if(result!=0){
                throw new SemanticError("Tipo da variavel se difere do esperado");
            }
            assembly.oneIndexStart(assembly,coutPile.get(position));
            assembly.addToIndex(coutPile.get(position2),coutPile.get(positionOps),assembly);
            for(int i=3;i<indexList.size();i=i+2){
                position = indexList.get(i+1).positionInCoutPile;
                positionOps = indexList.get(i).positionInCoutPile;
                result = semanticTable.resultType(result, helper.changeTypeStringToInt(coutPileOfTypes.get(position)),helper.changeOpsToInt(coutPileOfTypes.get(positionOps)));
                if(result!=0){
                    throw new SemanticError("Tipo da variavel se difere do esperado");
                }
                assembly.addToIndex(coutPile.get(position),coutPile.get(positionOps),assembly);
            }
        }

        assembly.endIndexMonoCout(coutPile.get((indexList.get(0).positionInCoutPile)-1),assembly);
    }
    private void operateWithIndexeCin() throws SemanticError {
        if(indexList.size()==1){
            int position = indexList.get(0).positionInCinPile;
            if(semanticTable.atribType(0,helper.changeTypeStringToInt(cinPileOfTypes.get(position))) !=0){
                throw new SemanticError("Tipo da variavel se difere do esperado");
            }
            assembly.oneIndexStart(assembly,cinPile.get(indexList.get(0).positionInCinPile));

        }
        else{
            int position = indexList.get(0).positionInCinPile;
            int position2 = indexList.get(2).positionInCinPile;
            int positionOps = indexList.get(1).positionInCinPile;
            int result = semanticTable.resultType(helper.changeTypeStringToInt(cinPileOfTypes.get(position)),helper.changeTypeStringToInt(cinPileOfTypes.get(position2)),helper.changeOpsToInt(cinPileOfTypes.get(positionOps)));
            if(result!=0){
                throw new SemanticError("Tipo da variavel se difere do esperado");
            }
            assembly.oneIndexStart(assembly,cinPile.get(position));
            assembly.addToIndex(cinPile.get(position2),cinPile.get(positionOps),assembly);
            for(int i=3;i<indexList.size();i=i+2){
                position = indexList.get(i+1).positionInCinPile;
                positionOps = indexList.get(i).positionInCinPile;
                result = semanticTable.resultType(result, helper.changeTypeStringToInt(cinPileOfTypes.get(position)),helper.changeOpsToInt(cinPileOfTypes.get(positionOps)));
                if(result!=0){
                    throw new SemanticError("Tipo da variavel se difere do esperado");
                }
                assembly.addToIndex(cinPile.get(position),cinPile.get(positionOps),assembly);
            }
        }

        assembly.endIndexCin(cinPile.get((indexList.get(0).positionInCinPile)-1),assembly);
    }
    private void operateWithIndexesMultiVecCout(int initialPointOfIndexList, int finalPointOfIndexList, int storeCount, int numberOfVecs) throws SemanticError {
        if(indexList.size()==1){
            int position = indexList.get(0).positionInCoutPile;
            if(semanticTable.atribType(0,helper.changeTypeStringToInt(coutPileOfTypes.get(position))) !=0){
                throw new SemanticError("Tipo da variavel se difere do esperado");
            }
            assembly.oneIndexStart(assembly,coutPile.get(indexList.get(0).positionInAttrPile));

        }
        else{
            int position;
            int position2;
            int positionOps;
            int result;
            if(initialPointOfIndexList!=finalPointOfIndexList) {
                position = initialPointOfIndexList;
                position2 = initialPointOfIndexList + 2;
                positionOps = initialPointOfIndexList + 1;
                result = semanticTable.resultType(helper.changeTypeStringToInt(coutPileOfTypes.get(position)), helper.changeTypeStringToInt(coutPileOfTypes.get(position2)), helper.changeOpsToInt(coutPileOfTypes.get(positionOps)));
                if (result != 0) {
                    throw new SemanticError("Tipo da variavel se difere do esperado");
                }
                assembly.oneIndexStart(assembly, coutPile.get(position));
                assembly.addToIndex(coutPile.get(position2), coutPile.get(positionOps), assembly);
            }
            //in case one element
            else{
                position = initialPointOfIndexList;
                result = semanticTable.atribType(0,helper.changeTypeStringToInt(coutPileOfTypes.get(position)));
                assembly.oneIndexStart(assembly, coutPile.get(position));
            }
            for(int i=initialPointOfIndexList+3;i<finalPointOfIndexList;i++){
                position = initialPointOfIndexList + i;
                positionOps = initialPointOfIndexList + i + 1;
                result = semanticTable.resultType(result, helper.changeTypeStringToInt(coutPileOfTypes.get(position)),helper.changeOpsToInt(coutPileOfTypes.get(positionOps)));
                if(result!=0){
                    throw new SemanticError("Tipo da variavel se difere do esperado");
                }
                assembly.addToIndex(coutPile.get(position),coutPile.get(positionOps),assembly);
            }
        }
        assembly.endIndex(coutPile.get(initialPointOfIndexList-1),assembly);
        assembly.storeMultVecCout(assembly,storeCount);
        if(storeCount>0){
            boolean isTheLastOne = false;
            String negativeOrPositive = coutPile.get(initialPointOfIndexList-2);
            if(storeCount+1==numberOfVecs){
                isTheLastOne = true;
            }
            assembly.sumOrRedOfVecs(assembly,negativeOrPositive,isTheLastOne);
        }
    }
    public void operateWithIndexesMultiVec(int initialPointOfIndexList, int finalPointOfIndexList, int storeCount, int numberOfVecs) throws SemanticError {
        if(indexList.size()==1){
            int position = indexList.get(0).positionInAttrPile;
            if(semanticTable.atribType(0,helper.changeTypeStringToInt(attrPile.get(position))) !=0){
                throw new SemanticError("Tipo da variavel se difere do esperado");
            }
            assembly.oneIndexStart(assembly,attrPileOfLex.get(indexList.get(0).positionInAttrPile));

        }
        else{
            int position;
            int position2;
            int positionOps;
            int result;
            if(initialPointOfIndexList!=finalPointOfIndexList) {
                 position = initialPointOfIndexList;
                 position2 = initialPointOfIndexList + 2;
                 positionOps = initialPointOfIndexList + 1;
                 result = semanticTable.resultType(helper.changeTypeStringToInt(attrPile.get(position)), helper.changeTypeStringToInt(attrPile.get(position2)), helper.changeOpsToInt(attrPile.get(positionOps)));
                if (result != 0) {
                    throw new SemanticError("Tipo da variavel se difere do esperado");
                }
                assembly.oneIndexStart(assembly, attrPileOfLex.get(position));
                assembly.addToIndex(attrPileOfLex.get(position2), attrPileOfLex.get(positionOps), assembly);
            }
            //in case one element
            else{
                position = initialPointOfIndexList;
                result = semanticTable.atribType(0,helper.changeTypeStringToInt(attrPile.get(position)));
                assembly.oneIndexStart(assembly, attrPileOfLex.get(position));
            }
            for(int i=initialPointOfIndexList+3;i<finalPointOfIndexList;i++){
                position = initialPointOfIndexList + i;
                positionOps = initialPointOfIndexList + i + 1;
                result = semanticTable.resultType(result, helper.changeTypeStringToInt(attrPile.get(position)),helper.changeOpsToInt(attrPile.get(positionOps)));
                if(result!=0){
                    throw new SemanticError("Tipo da variavel se difere do esperado");
                }
                assembly.addToIndex(attrPileOfLex.get(position),attrPileOfLex.get(positionOps),assembly);
            }
        }
        assembly.endIndex(attrPileOfLex.get(initialPointOfIndexList-1),assembly);
        assembly.storeMultVec(assembly,storeCount);
        if(storeCount>0){
            boolean isTheLastOne = false;
            String negativeOrPositive = attrPileOfLex.get(initialPointOfIndexList-2);
            if(storeCount+1==numberOfVecs){
                isTheLastOne = true;
            }
            assembly.sumOrRedOfVecs(assembly,negativeOrPositive,isTheLastOne);
        }
    }

    private List<Integer> getAllTheNonIndexesOrVec(){
        List<Integer> nonVecRelated = new ArrayList<>();
        //inserting all
        for(int i =0;i<attrPile.size();i++){nonVecRelated.add(i);}
        //setting indexes to remove

        ///FOR 1 VEC SPECIF
        for(int i=0;i<indexList.size();i++){
            nonVecRelated.set(indexList.get(i).positionInAttrPile,-1);
        }
        //removing vectors
        for(int i=0;i<nonVecRelated.size();i++){
            if(nonVecRelated.get(i)!=-1){
                if(checkIfElementIsVect(symbolTable,attrPileOfLex.get(nonVecRelated.get(i)))){
                    nonVecRelated.set(nonVecRelated.get(i),-1);
                }
            }
        }
        nonVecRelated.removeIf( name -> name.equals(-1));

        for(int i =0;i<nonVecRelated.size();i++){
            for(String ops : listOfOps){
                if(attrPileOfLex.get(nonVecRelated.get(i)).equals(ops)){
                    nonVecRelated.set(i,-1);
                    break;
                }
            }
        }
        nonVecRelated.removeIf( name -> name.equals(-1));
        return nonVecRelated;
    }
    private void handleRegularExpression() throws SemanticError {
        List<Integer> nonVecRelated = getAllTheNonIndexesOrVec();
        get2by2_Ops(0,0,nonVecRelated);
    }
    public ArrayList<IndexHelper> getFirstPositionOfIndex(){
        ArrayList<IndexHelper> listOfFirstIndexes = new ArrayList<>();
        String currentFather = indexList.get(0).fatherPosition;
        listOfFirstIndexes.add(indexList.get(0));
        for(IndexHelper index : indexList){
            if(!index.fatherPosition.equals(currentFather)){
                currentFather=index.fatherPosition;
                listOfFirstIndexes.add(index);
            }
        }
        return listOfFirstIndexes;
    }
    public ArrayList<IndexHelper> getLastPositionOfIndex(){
        ArrayList<IndexHelper> listOfLastIndexes = new ArrayList<>();
        String currentFather = indexList.get(0).fatherPosition;
        for(int i =0;i<indexList.size();i++){
            if(i+1<indexList.size()){
                if(!indexList.get(i+1).fatherPosition.equals(currentFather)){
                    listOfLastIndexes.add(indexList.get(i));
                    currentFather = indexList.get(i+1).fatherPosition;
                }
            }
        }
        //Adding the last one because the for does not pick it
        listOfLastIndexes.add(indexList.get(indexList.size()-1));
        return  listOfLastIndexes;
    }

    private void treatingVects(boolean leftIsVec) throws SemanticError {
        int numberOfVecs = checkNumberOfVecsInPile(attrPileOfLex);
        ArrayList<IndexHelper> listOfFirst = getFirstPositionOfIndex();
        ArrayList<IndexHelper> listOfLast = getLastPositionOfIndex();
        storeCount = 0;
        for(int i =0;i<numberOfVecs;i++){
            if(!leftIsVec){
            operateWithIndexesMultiVec(listOfFirst.get(i).positionInAttrPile,listOfLast.get(i).positionInAttrPile,storeCount,numberOfVecs);
            }
            else {
                operateWithIndexesMultiVecForLeftSideArr(listOfFirst.get(i).positionInAttrPile,listOfLast.get(i).positionInAttrPile,storeCount,numberOfVecs);
            }
            storeCount++;
        }
    }
    private ArrayList<Integer> checkNumberOfExpInPileCout(){
        ArrayList<Integer> listOfExp = new ArrayList<>();
        //fill list
        for(int i =0;i<coutPile.size();i++){
            listOfExp.add(i);
        }
        //check for elements inside indexList
        for(int x : listOfExp){
            for(IndexHelper index : indexList){
                if(index.positionInCoutPile==x){
                    listOfExp.set(x,-1);
                }
            }
        }
        for(int x:listOfExp){
            if(x>=0){
                if(checkIfElementIsVect(symbolTable,attrPileOfLex.get(x))){
                    listOfExp.set(x,-1);
                }
            }
        }
        listOfExp.removeIf( name -> name.equals(-1));

        //Getting sums and red and deleting those who does not belong exp
        for(int i =0;i<listOfExp.size();i++){
            for(String ops : listOfOps){
                if(attrPileOfLex.get(listOfExp.get(i)).equals(ops)){
                    listOfExp.set(i,-1);
                    break;
                }
            }
        }
        listOfExp.removeIf( name -> name.equals(-1));


        return listOfExp;
    }
    public ArrayList<Integer> checkNumberOfExpInPile(){
        ArrayList<Integer> listOfExp = new ArrayList<>();
        //fill list
        for(int i =0;i<attrPile.size();i++){
            listOfExp.add(i);
        }
        //check for elements inside indexList
        for(int x : listOfExp){
            for(IndexHelper index : indexList){
                if(index.positionInAttrPile==x){
                    listOfExp.set(x,-1);
                }
            }
        }
        for(int x:listOfExp){
            if(x>=0){
                if(checkIfElementIsVect(symbolTable,attrPileOfLex.get(x))){
                    listOfExp.set(x,-1);
                }
            }
        }
        listOfExp.removeIf( name -> name.equals(-1));

        //Getting sums and red and deleting those who does not belong exp
        for(int i =0;i<listOfExp.size();i++){
            for(String ops : listOfOps){
                if(attrPileOfLex.get(listOfExp.get(i)).equals(ops)){
                    listOfExp.set(i,-1);
                    break;
                }
            }
        }
        listOfExp.removeIf( name -> name.equals(-1));


        return listOfExp;
    }
    private void treatingExp(){
        ArrayList<Integer> numberOfExp;
        if(isCout){
            numberOfExp=checkNumberOfExpInPileCout();
        }
        else{
          numberOfExp= checkNumberOfExpInPile();
        }
        //lembrar de tratar caso seja o primeiro da pilha e nao tenha + ou - na frente
        for(int i=0;i<numberOfExp.size();i++){
            String ops;
            if(numberOfExp.get(i)==0){
                ops="+";
            }else{
                ops= attrPileOfLex.get(numberOfExp.get(i)-1);
            }
            assembly.addToIndex(attrPileOfLex.get(numberOfExp.get(i)),ops,assembly);
        }
    }

    private void checkAttr() throws SemanticError {

        if(attrPile.size()==1){
            checkAttrCase1();
        }
        //In case elem is vec
        if(attrPile.size()==2 && checkIfElementIsVect(symbolTable,attrPileOfLex.get(0))){
            checkAttrCase2();
        }
        if(attrPile.size()>2){
            ///Checking if there is more than one vector with
            int qntElementsAreVec = checkNumberOfVecsInPile(attrPileOfLex);
            ///1 vec with exp index
            if(attrPileOfLex.size()==indexExp.size()+1){
                if(leftElementIsVect){
                    //if is only one index
                    assembly.loadLeftSide(assembly,indexOfAttr);
                }
                int lastResult = getFirst3_Ops_Index();
                String var = attrPileOfLex.get(0);
                assembly.indexOfVecExp(assembly,attrPileOfLex.get(3),attrPileOfLex.get(1),attrPileOfLex.get(2),attrPileOfLex.get(0));
                for(int i =0;i<3;i++){
                    attrPile.remove(0);
                    attrPileOfLex.remove(0);
                }
                get2by2_Ops(lastResult,1);
                assembly.endIndex(var,assembly);

                if(leftElementIsVect){
                    assembly.vecReceivesVec(assembly);
                }
            }
            //treats vec with or without exp
            else if(qntElementsAreVec>1){
                if (leftElementIsVect) {
                    assembly.loadLeftSide(assembly,indexOfAttr);
                    treatingVects(true);

                }else {
                    treatingVects(false);
                    treatingExp();
                }
            }
            else if(qntElementsAreVec==1 && indexExp.size()+1!=attrPileOfLex.size()){
                operateWithIndexes();
                handleRegularExpression();
            }
            ///regular exp
            else{
                int lastResult = getFirst3_Ops(attrPile.get(0),attrPile.get(1),attrPile.get(2));
                assembly.simpleVarReceivesIntOrVar(attrPileOfLex.get(0),assembly);
                assembly.addTo(attrPileOfLex.get(2),attrPileOfLex.get(1),assembly);
                for(int i =0;i<3;i++){
                    attrPile.remove(0);
                    attrPileOfLex.remove(0);
                }
                get2by2_Ops(lastResult,0);

            }

        }
    }
    private int positionOfLastAddedVec(List<Simbolo> symbolTable, List<String>pile1,List<String> pile2){
        for(int i =pile1.size()-1;i>=0;i--){
            if(checkIfElementIsVect(symbolTable,pile2.get(i))){
                return i;
            }
        }
        return -1;
    }
    public void writeVec(){
        assembly.writeIfElIsVec(assembly,coutPile.get(1),coutPile.get(0));
    }
    public void writeVarOrInt(){
        assembly.writeVarOrInt(assembly,coutPile.get(0));
    }
    private void coutHandleVecs(int numberOfVecs) throws SemanticError {
        ArrayList<IndexHelper> listOfFirst = getFirstPositionOfIndex();
        ArrayList<IndexHelper> listOfLast = getLastPositionOfIndex();
        storeCount = 0;
        for(int i =0;i<numberOfVecs;i++){
            operateWithIndexesMultiVecCout(listOfFirst.get(i).positionInCoutPile,listOfLast.get(i).positionInCoutPile,storeCount,numberOfVecs);
            storeCount++;
        }
        treatingExp();
        assembly.storeCout(assembly);
    }
    private void checkCout() throws SemanticError {
        if(coutPile.size()==1){
            writeVarOrInt();
        }
        if(coutPile.size()==2){
            writeVec();
        }
        if(coutPile.size()>2){
            int numberOfVecs = checkNumberOfVecsInPile(coutPile);
            if(numberOfVecs>1){
                coutHandleVecs(numberOfVecs);
            }else{
                operateWithIndexeCout();
            }

        }
    }

    private void readVarOrInt(){
        assembly.readVarOrInt(assembly,cinPile.get(0));
    }
    private void readVec(){
        assembly.readIfElIsVec(assembly,cinPile.get(0),cinPile.get(1));
    }
    private  void readArrWithExpIndex() throws SemanticError {
        treatingVects();
    }
    private void checkCin() throws SemanticError {
        if(cinPile.size()==1){
            readVarOrInt();
        }
        if(cinPile.size()==2){
            readVec();
        }
        if(cinPile.size()>2){
            readArrWithExpIndex();
        }
    }
    private void treatingVects() throws SemanticError {
            operateWithIndexeCin();
    }
    public void executeAction(int action, Token token) throws SemanticError, SyntaticError {
        System.out.println(action + " " + token);

        if(action==34 && attrPile.size()>0) {
            checkAttr();
            assembly.store(elementOnTheLeftSideOfAttr.name,assembly,leftElementIsVect);
            changedToInit(elementOnTheLeftSideOfAttr.name);
            attrPile.clear();
            attrPileOfLex.clear();
        }
        if(action==34 && coutPile.size()>0){
            checkCout();
        }
        if(action==34 && cinPile.size()>0){
            checkCin();
        }

        else if(action == 25 && expPilesOfBranches.size()>0){
            Main.branch.ifCondition();
        }
        else if(endElse && action==33){
            Main.branch.end("end_else");
        }
        //condition alone
        else if(globalLastAction==33 && Main.branch.branchPile.size()>0 && action!=45 && !endElse && !isDo && !Main.branch.branchPile.get(Main.branch.branchPile.size()-1).lastBranchType.equals("for")){
            Main.branch.end("condition");
        }
        //if with else
        else if(globalLastAction==33 && Main.branch.branchPile.size()>0 && action==45 && !endElse && !isDo && !Main.branch.branchPile.get(Main.branch.branchPile.size()-1).lastBranchType.equals("for")){
            Main.branch.end("starting_else");
        }
        else if(globalLastAction==20){
            Main.branch.justDOit();
        }
        else if(globalLastAction==29 && isFor){
            firstElementOfFor=Main.branch.forExp();
        }
        else if(isFor && action==33 && Main.branch.branchPile.get(Main.branch.branchPile.size()-1).lastBranchType.equals("for")){
            Main.branch.endFor(firstElementOfFor);
        }



        switch(action){
            case -2:
                pileOfScopes.add(0);
                break;
            case 1: {
                this.type = token.getLexeme();
                if(isParameter){
                    Function x = Main.func.listOfFunctions.get(Main.func.listOfFunctions.size()-1);
                    x.parameterList.add(this.type);
                }
                break;
            }
            case 3:
                Simbolo vet = new Simbolo();
                vet.name =token.getLexeme();
                vet.type = this.type;
                vet.vect = true;
                vet.scope = pileOfScopes.get(pileOfScopes.size()-1);
                lastAddSymbol=vet;
                if(!checkIfVarExistsCurrentContext(vet)){
                    symbolTable.add(vet);
                }else{
                    throw new SemanticError("Variavel ja declarada neste escopo");
                }

                break;
            //TYPE
            case 4:{
                Simbolo sim = new Simbolo();
                sim.name = token.getLexeme();
                sim.type = this.type;
                sim.scope = pileOfScopes.get(pileOfScopes.size()-1);
                lastAddSymbol=sim;
                elementOnTheLeftSideOfAttr = sim;
                if(!checkIfVarExistsCurrentContext(sim) && isAttr) {
                    attrPile.add(sim.type);
                    attrPileOfLex.add(token.getLexeme());
                }
                else if(!checkIfVarExistsCurrentContext(sim) && !isAttr){
                    symbolTable.add(sim);
                }
                else{
                    throw new SemanticError("Variavel ja declarada neste escopo");
                }
                break;
            }
            case 5:
                Simbolo par = new Simbolo();
                par.name = token.getLexeme();
                par.type = this.type;
                par.param = true;
                par.scope = pileOfScopes.size()-1;
                symbolTable.add(par);
                if(isParameter){
                    Function x = Main.func.listOfFunctions.get(Main.func.listOfFunctions.size()-1);
                    x.parameterList.add(par.name);
                }
                break;
            case 6:
                Simbolo func = new Simbolo();
                func.name = token.getLexeme();
                func.scope = pileOfScopes.get(pileOfScopes.size()-1);
                if(!checkIfVarExists(func)){
                    throw new SemanticError("Funcao inexistente");
                }
                calledFunction=func.name;
                break;
            case 7:
                Simbolo parArr = new Simbolo();
                parArr.name = token.getLexeme();
                parArr.type = this.type;
                parArr.param = true;
                parArr.scope = pileOfScopes.size();
                parArr.vect = true;
                symbolTable.add(parArr);
                break;
            case 8://adding var
                String name = token.getLexeme();
                String type = returnObjType(name);

                Simbolo x = new Simbolo();
                x.scope =pileOfScopes.get(pileOfScopes.size()-1);
                x.type = type;
                x.name = name;
                if(!checkIfVarExistsCurrentContext(x) && !isDo && !isBranch && !isFor && !Main.func.listOfFunctions.get(Main.func.listOfFunctions.size()-1).parameterList.contains(name)){
                    throw new SemanticError("A variavel sendo usada nao foi declarada");
                }else{
                    changedToUsed(x);
                }
                if(!checkIfIsInit(x)){
                    System.out.println("Uso de variavel nao inicializada");
                    warningOutput += "Uso de variavel nao inicializada\n";
                }
                if(isAttr){
                    attrPile.add(type);
                    attrPileOfLex.add(token.getLexeme());
                }
                if(isCout){
                    coutPile.add(token.getLexeme());
                    attrPileOfLex.add(token.getLexeme());
                    coutPileOfTypes.add(type);
                }
                if(vecExp && isCout){
                    IndexHelper index = new IndexHelper();
                    index.name= token.getLexeme();
                    index.fatherPosition= Integer.toString(positionOfLastAddedVec(symbolTable,coutPile,coutPile));
                    index.positionInCoutPile=coutPile.size()-1;
                    indexList.add(index);
                }

                if(isCin){
                    cinPile.add(token.getLexeme());
                    attrPileOfLex.add(token.getLexeme());
                    cinPileOfTypes.add(type);
                }
                if(vecExp && isCin){
                    IndexHelper index = new IndexHelper();
                    index.name= token.getLexeme();
                    index.fatherPosition= Integer.toString(positionOfLastAddedVec(symbolTable,cinPile,cinPile));
                    index.positionInCinPile=cinPile.size()-1;
                    indexList.add(index);
                }
                if(vecExp){
                    indexExp.add(token.getLexeme());
                }
                if(vecExp && isAttr){
                    IndexHelper index = new IndexHelper();
                    index.name= token.getLexeme();
                    index.fatherPosition= Integer.toString(positionOfLastAddedVec(symbolTable,attrPile,attrPileOfLex));
                    index.positionInAttrPile=attrPile.size()-1;
                    indexList.add(index);
                }
                if(isBranch){
                    expPilesOfBranches.add(type);
                    attrPileOfLex.add(token.getLexeme());
                }
                if(vecExp && isBranch){
                    IndexHelper index = new IndexHelper();
                    index.name= token.getLexeme();
                    index.fatherPosition= Integer.toString(positionOfLastAddedVec(symbolTable,expPilesOfBranches,attrPileOfLex));
                    index.positionInBranch=expPilesOfBranches.size()-1;
                    indexList.add(index);
                }


                break;
            case 9://adding int
                if(isAttr){
                    attrPile.add("int");
                    attrPileOfLex.add(token.getLexeme());
                }
                if(isCout){
                    coutPile.add(token.getLexeme());
                    attrPileOfLex.add(token.getLexeme());
                    coutPileOfTypes.add("int");
                }
                if(vecExp && isCout){
                    IndexHelper index = new IndexHelper();
                    index.name= token.getLexeme();
                    index.fatherPosition= Integer.toString(positionOfLastAddedVec(symbolTable,coutPile,coutPile));
                    index.positionInCoutPile=coutPile.size()-1;
                    indexList.add(index);
                }
                if(isCin){
                    cinPile.add(token.getLexeme());
                    attrPileOfLex.add(token.getLexeme());
                    cinPileOfTypes.add("int");
                }
                if(vecExp && isCin){
                    IndexHelper index = new IndexHelper();
                    index.name= token.getLexeme();
                    index.fatherPosition= Integer.toString(positionOfLastAddedVec(symbolTable,cinPile,cinPile));
                    index.positionInCinPile=cinPile.size()-1;
                    indexList.add(index);
                }
                if(vecExp){
                    indexExp.add(token.getLexeme());
                }
                if(vecExp && isAttr){
                    IndexHelper index = new IndexHelper();
                    index.name= token.getLexeme();
                    index.fatherPosition= Integer.toString(positionOfLastAddedVec(symbolTable,attrPile,attrPileOfLex));
                    index.positionInAttrPile=attrPile.size()-1;
                    indexList.add(index);
                }
                if(isBranch){
                    expPilesOfBranches.add("int");
                    attrPileOfLex.add(token.getLexeme());
                }
                if(vecExp && isBranch){
                    IndexHelper index = new IndexHelper();
                    index.name= token.getLexeme();
                    index.fatherPosition= Integer.toString(positionOfLastAddedVec(symbolTable,expPilesOfBranches,attrPileOfLex));
                    index.positionInBranch=expPilesOfBranches.size()-1;
                    indexList.add(index);
                }

                break;


            case 10://float
                if(isAttr){
                    attrPile.add("float");
                }
                break;
            case 11://char
                if(isAttr){
                    attrPile.add("char");
                }
                break;
            case 12://ops
                if(isAttr){
                    attrPile.add(token.getLexeme());
                    attrPileOfLex.add(token.getLexeme());
                }
                if(vecExp && isAttr){
                    IndexHelper index = new IndexHelper();
                    indexExp.add(token.getLexeme());
                    index.name= token.getLexeme();
                    index.fatherPosition= Integer.toString(positionOfLastAddedVec(symbolTable,attrPile,attrPileOfLex));
                    index.positionInAttrPile=attrPile.size()-1;
                    indexList.add(index);
                }
                if(isCout){
                    attrPileOfLex.add(token.getLexeme());
                    coutPile.add(token.getLexeme());
                    coutPileOfTypes.add(token.getLexeme());

                }
                if(vecExp && isCout){
                    IndexHelper index = new IndexHelper();
                    index.name= token.getLexeme();
                    index.fatherPosition= Integer.toString(positionOfLastAddedVec(symbolTable,coutPile,coutPile));
                    index.positionInCoutPile=coutPile.size()-1;
                    indexList.add(index);
                }
                if(vecExp && isCin){
                    cinPile.add(token.getLexeme());
                    cinPileOfTypes.add(token.getLexeme());
                    IndexHelper index = new IndexHelper();
                    index.name= token.getLexeme();
                    index.fatherPosition= Integer.toString(positionOfLastAddedVec(symbolTable,cinPile,cinPile));
                    index.positionInCinPile=cinPile.size()-1;
                    indexList.add(index);
                }
                if(isBranch){
                    expPilesOfBranches.add(token.getLexeme());
                    attrPileOfLex.add(token.getLexeme());
                }

                break;
            case 13://string
                if(isAttr){
                    attrPile.add("string");
                }
                break;
            case 14://
                isVect = true;
                indexOfAttr=token.getLexeme();
                break;
            case 15://vet
                Simbolo t = getSimbol(token.getLexeme(),pileOfScopes.get(pileOfScopes.size() - 1));
                if(t==null){
                    throw new SemanticError("Variavel nao declarada");
                }
                elementOnTheLeftSideOfAttr = t;
                leftElementIsVect=true;
                break;
            case 16:
                calledFunction = token.getLexeme();
                break;
            case 17://Function declare
                Simbolo functionSymb1 = new Simbolo();
                functionSymb1.name = token.getLexeme();
                functionSymb1.type = this.type;
                functionSymb1.func = true;
                functionSymb1.scope = pileOfScopes.get(pileOfScopes.size()-1);
                isFunc = true;
                if(!checkIfVarExistsCurrentContext(functionSymb1)){
                    symbolTable.add(functionSymb1);
                    Function func_ = new Function();
                    func_.name = functionSymb1.name;
                    Main.func.listOfFunctions.add(func_);

                }else{
                    throw new SemanticError("Funcao ja declarada neste escopo");
                }
                break;
            case 19:
                isBranch = true;
                isWhile = true;
                expPilesOfBranches.clear();
                break;
            case 20:
                isDo = true;
                expPilesOfBranches.clear();
                break;
            case 21:
                isFor = true;
                isBranch=true;
                expPilesOfBranches.clear();
                break;
            case 22:
                isBranch = true;
                isIf=true;
                break;
            case 23://bool
                if(isAttr){
                    attrPile.add("boolean");
                }
                break;
            case 24://Initial phase of attrib

                // NEED TO CHANGE ----------------- CHECK FOR TYPE ISSUES
                isAttr = true;
                isVect = false;
                break;
            case 25:
                isBranch= false;
                expPilesOfBranches.clear();
                attrPileOfLex.clear();
                break;
            case 32://Scope open
                scopeCounter++;
                pileOfScopes.add(scopeCounter);
                if(isFor){
                    attrPileOfLex.clear();
                    isBranch=false;

                }
                break;
            case 33://Scope close
                scopeCounter--;
                pileOfScopes.remove(this.pileOfScopes.size()-1);
                if(isFunc){
                    Main.func.endFuncProcess();
                    isFunc = false;
                }
                break;
            case 34://Attrib close
                isAttr=false;
                isCout=false;
                isCin =false;
                leftElementIsVect=false;
                indexList.clear();
                indexExp.clear();
                cinPileOfTypes.clear();
                cinPile.clear();
                coutPile.clear();
                coutPileOfTypes.clear();
                isWhile = false;
                isIf = false;
                break;
            case 35:
                isCout = true;
                break;
            case 36:
                isCin = true;
                break;
            case 37:
                changeSize(Integer.parseInt(token.getLexeme()), lastAddSymbol.scope, lastAddSymbol.name);
                break;
            case 38:
                vecExp = true;
                break;
            case 39:
                vecExp = false;
                break;
            case 41:
                if(isBranch){
                    expPilesOfBranches.add(token.getLexeme());
                    attrPileOfLex.add(token.getLexeme());
                }
                break;
            case 44:
                if(isBranch){
                    expPilesOfBranches.add(token.getLexeme());
                    attrPileOfLex.add(token.getLexeme());
                }
                break;
            case 46:
                if(isBranch){
                    expPilesOfBranches.add(token.getLexeme());
                    attrPileOfLex.add(token.getLexeme());
                }
                break;
            case 47:
                isParameter = true;
                if(isFunc) {
                    Main.func.startProcess(Main.func.listOfFunctions.get(Main.func.listOfFunctions.size()-1).name);
                }
            break;
            case 48:
                isParameter=false;
                if(isFunc) {
                    Main.func.continueProcessParameter();
                }
                break;
            case 50:
                Main.func.callFunc();
                break;
            case 51:
                calledParameters.add(token.getLexeme());
                break;
        }
        globalLastAction = action;

    }
}
