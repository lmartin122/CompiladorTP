//### This file created by BYACC 1.8(/Java extension  1.15)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//###           06 Aug 00  -- Bob Jamison -- made state variables class-global
//###           03 Jan 01  -- Bob Jamison -- improved flags, tracing
//###           16 May 01  -- Bob Jamison -- added custom stack sizing
//###           04 Mar 02  -- Yuval Oren  -- improved java performance, added options
//###           14 Mar 02  -- Tomas Hurka -- -d support, static initializer workaround
//### Please send bug reports to tom@hukatronic.cz
//### static char yysccsid[] = "@(#)yaccpar	1.8 (Berkeley) 01/20/90";






//#line 2 "grammer.y"
package Sintactico;
import Lexico.AnalizadorLexico;
import java.util.Scanner;
import Tools.Logger;
import java.util.ArrayList;
import java.io.File;
import Tools.Tupla;
import Tools.TablaSimbolos;
import java.io.IOException;
//#line 27 "Parser.java"




public class Parser
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character

//########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
void debug(String msg)
{
  if (yydebug)
    System.out.println(msg);
}

//########## STATE STACK ##########
final static int YYSTACKSIZE = 500;  //maximum stack size
int statestk[] = new int[YYSTACKSIZE]; //state stack
int stateptr;
int stateptrmax;                     //highest index of stackptr
int statemax;                        //state when highest index reached
//###############################################################
// methods: state stack push,pop,drop,peek
//###############################################################
final void state_push(int state)
{
  try {
		stateptr++;
		statestk[stateptr]=state;
	 }
	 catch (ArrayIndexOutOfBoundsException e) {
     int oldsize = statestk.length;
     int newsize = oldsize * 2;
     int[] newstack = new int[newsize];
     System.arraycopy(statestk,0,newstack,0,oldsize);
     statestk = newstack;
     statestk[stateptr]=state;
  }
}
final int state_pop()
{
  return statestk[stateptr--];
}
final void state_drop(int cnt)
{
  stateptr -= cnt; 
}
final int state_peek(int relative)
{
  return statestk[stateptr-relative];
}
//###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
final boolean init_stacks()
{
  stateptr = -1;
  val_init();
  return true;
}
//###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
void dump_stacks(int count)
{
int i;
  System.out.println("=index==state====value=     s:"+stateptr+"  v:"+valptr);
  for (i=0;i<count;i++)
    System.out.println(" "+i+"    "+statestk[i]+"      "+valstk[i]);
  System.out.println("======================");
}


//########## SEMANTIC VALUES ##########
//public class ParserVal is defined in ParserVal.java


String   yytext;//user variable to return contextual strings
ParserVal yyval; //used to return semantic vals from action routines
ParserVal yylval;//the 'lval' (result) I got from yylex()
ParserVal valstk[];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
void val_init()
{
  valstk=new ParserVal[YYSTACKSIZE];
  yyval=new ParserVal();
  yylval=new ParserVal();
  valptr=-1;
}
void val_push(ParserVal val)
{
  if (valptr>=YYSTACKSIZE)
    return;
  valstk[++valptr]=val;
}
ParserVal val_pop()
{
  if (valptr<0)
    return new ParserVal();
  return valstk[valptr--];
}
void val_drop(int cnt)
{
int ptr;
  ptr=valptr-cnt;
  if (ptr<0)
    return;
  valptr = ptr;
}
ParserVal val_peek(int relative)
{
int ptr;
  ptr=valptr-relative;
  if (ptr<0)
    return new ParserVal();
  return valstk[ptr];
}
final ParserVal dup_yyval(ParserVal val)
{
  ParserVal dup = new ParserVal();
  dup.ival = val.ival;
  dup.dval = val.dval;
  dup.sval = val.sval;
  dup.obj = val.obj;
  return dup;
}
//#### end semantic value section ####
public final static short CLASS=257;
public final static short INTERFACE=258;
public final static short IMPLEMENT=259;
public final static short RETURN=260;
public final static short IF=261;
public final static short ELSE=262;
public final static short END_IF=263;
public final static short FOR=264;
public final static short IN=265;
public final static short RANGE=266;
public final static short IMPL=267;
public final static short PRINT=268;
public final static short TOD=269;
public final static short EQUAL_OPERATOR=270;
public final static short NOT_EQUAL_OPERATOR=271;
public final static short GREATER_THAN_OR_EQUAL_OPERATOR=272;
public final static short LESS_THAN_OR_EQUAL_OPERATOR=273;
public final static short MINUS_ASSIGN=274;
public final static short VOID=275;
public final static short LONG=276;
public final static short UINT=277;
public final static short DOUBLE=278;
public final static short CADENA=279;
public final static short ID=280;
public final static short CTE_DOUBLE=281;
public final static short CTE_UINT=282;
public final static short CTE_LONG=283;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    0,    1,    1,    2,    2,    2,    2,    3,    3,
    7,    7,    9,    9,   10,   11,   11,   12,   12,   15,
   15,   16,   16,   16,   16,   16,   16,   16,   17,   18,
   13,   20,   22,   23,   23,   23,   23,   23,   21,   21,
   24,   26,    8,   27,   27,   27,    4,   29,   29,   29,
   29,   30,   30,   31,   31,   32,   33,    5,    5,    5,
    5,    5,   35,   35,   36,   36,   37,   38,   39,   39,
   40,   41,   41,   43,   44,   44,   45,   45,   45,   46,
   46,   46,   46,   46,   19,   19,   19,   19,   19,   19,
   19,   47,   47,   47,   48,   48,   48,   48,   49,   49,
   49,   49,   50,   50,   50,   50,   50,   50,   42,   42,
   42,   42,   42,   42,   42,   51,   51,   51,   51,   51,
   51,   14,   14,   52,   34,   53,   53,   54,   54,   55,
   28,   25,   25,   25,   25,   25,   25,   25,   57,   57,
   56,   56,   58,   58,    6,    6,   59,   59,   59,   59,
   59,   59,   60,   68,   61,   61,   61,   61,   61,   61,
   69,   69,   69,   66,   66,   70,   70,   67,   62,   62,
   62,   62,   62,   62,   62,   63,   63,   63,   63,   63,
   63,   63,   63,   63,   64,   64,   64,   64,   64,   64,
   64,   64,   64,   64,   64,   71,   72,   74,   73,   65,
   65,   65,   65,   65,
};
final static short yylen[] = {                            2,
    1,    1,    1,    2,    1,    1,    1,    1,    3,    4,
    3,    3,    1,    2,    1,    1,    1,    3,    2,    1,
    3,    1,    3,    4,    3,    3,    3,    3,    1,    1,
    2,    2,    1,    4,    4,    5,    3,    3,    1,    1,
    2,    1,    2,    1,    3,    3,    3,    3,    3,    2,
    2,    1,    2,    1,    1,    2,    3,    5,    6,    6,
    6,    4,    3,    3,    1,    2,    1,    2,    1,    1,
    3,    1,    1,    3,    1,    1,    1,    3,    3,    1,
    3,    3,    3,    3,    1,    4,    4,    4,    3,    3,
    1,    1,    3,    3,    1,    3,    3,    3,    1,    3,
    2,    1,    1,    1,    1,    2,    2,    2,    1,    1,
    2,    1,    1,    1,    1,    4,    3,    5,    4,    3,
    5,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    5,    3,    5,    4,    4,    2,    2,    3,    2,
    1,    2,    1,    2,    1,    1,    1,    1,    1,    1,
    1,    1,    2,    2,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    2,    2,    1,    1,    1,    7,    7,
    6,    6,    7,    7,    7,    9,    9,    9,    9,    9,
    9,    9,    9,    9,   12,   12,   12,   12,   13,    8,
    8,   11,   11,   11,   11,    1,    1,    1,    1,    3,
    3,    3,    3,    2,
};
final static short yydefred[] = {                         0,
    0,    0,    0,    0,    0,    0,    0,   33,  129,  128,
  130,    0,    0,    0,  168,    0,    0,    3,    5,    6,
    7,    8,  159,    0,    0,    0,  161,    0,  166,    0,
    0,    0,  167,  122,  124,  126,  127,  145,  146,  156,
  157,  158,  160,  163,  162,    0,  155,    0,    0,    0,
  196,    0,    0,    0,    0,  125,    0,    0,    0,    0,
  204,    0,    0,    0,  137,  141,    0,    0,  138,    0,
    0,    4,   29,    0,   20,    0,   40,   31,   39,    0,
   32,    0,  112,  113,  115,  114,  110,  109,    0,    0,
    0,  153,  164,  165,  203,    0,    0,    0,    0,    0,
    9,    0,    0,    0,   47,    0,  102,  103,  104,  105,
    0,    0,    0,    0,    0,    0,   95,   99,    0,    0,
    0,  202,  201,  200,   91,    0,  117,   42,    0,    0,
    0,    0,  133,  142,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  111,   71,  120,    0,   74,
    0,    0,   62,    0,  131,    0,   44,    0,   13,   15,
   16,   17,    0,  123,    0,   10,   50,    0,    0,    0,
   52,   54,   55,   51,    0,    0,  106,  108,  107,  101,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  116,  135,    0,  136,    0,   21,    0,   25,   30,   26,
   28,   27,   23,   38,    0,    0,   37,    0,    0,  119,
    0,    0,   65,   67,    0,  197,    0,    0,    0,   11,
   14,    0,   12,    0,    0,   48,   53,   49,    0,    0,
    0,    0,  147,  148,  149,  150,  151,  152,  100,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
   96,   97,   98,    0,    0,    0,    0,   58,    0,   89,
    0,   90,    0,  118,  132,  134,   24,   41,   35,    0,
   34,  121,   70,   69,   68,   63,   66,   64,    0,    0,
   46,   45,   18,   57,  140,    0,  143,    0,    0,    0,
    0,    0,    0,    0,  172,    0,    0,  171,    0,    0,
   60,   61,   59,   88,   87,   86,   36,  199,    0,    0,
  139,  144,    0,    0,  174,    0,    0,  175,  173,    0,
    0,  169,    0,    0,  170,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  190,
  191,    0,    0,  198,    0,    0,  181,  182,  184,  183,
  176,  177,  180,  179,  178,    0,    0,    0,    0,    0,
    0,  194,  195,  192,  193,    0,    0,  187,  188,    0,
  185,  186,  189,
};
final static short yydgoto[] = {                         16,
   17,   18,   19,   20,   21,   22,  101,  102,  158,  159,
  160,  161,   23,   24,   74,   75,   76,  208,  209,   25,
   78,   26,   81,  216,   27,  129,  156,  157,  105,  170,
  171,  172,  173,  240,  153,  222,  223,  224,  285,   29,
   30,   89,   31,   32,  113,  114,  130,  116,  117,  118,
   33,   34,   35,   36,   37,   67,  241,  296,  242,   38,
   39,  243,  244,  245,  246,  247,  248,   46,   47,   48,
   52,  227,  319,  355,
};
final static short yysindex[] = {                        49,
 -122, -223, -203,   69, -193, -142,    3,    0,    0,    0,
    0,   91,   99,  129,    0,    0,   74,    0,    0,    0,
    0,    0,    0, -136,  -12, -119,    0,    0,    0,  333,
  114,  140,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  134,    0,    4,  157,  136,
    0,  -53,  -33,  -21,   15,    0,  -60, -218,  168,  -15,
    0,  -36,  -81,  173,    0,    0,  156,  177,    0,  162,
 -122,    0,    0,  160,    0,  387,    0,    0,    0,   -7,
    0,  164,    0,    0,    0,    0,    0,    0,   -5,  -20,
  -30,    0,    0,    0,    0,    2,  195,    8,  -96,  -96,
    0,    5,  -88,  182,    0,  215,    0,    0,    0,    0,
 -162,  -14,  -31,  123,   22,  113,    0,    0,   14,  212,
  -45,    0,    0,    0,    0,    6,    0,    0,  -24,   22,
  174,  249,    0,    0,  268,  270, -136,  262,   -5,   -5,
   -5,   -5,   -5, -113,  207,    0,    0,    0,  -18,    0,
   53,   53,    0,   -9,    0,   44,    0,  286,    0,    0,
    0,    0, -136,    0,  196,    0,    0, -136, -119,  310,
    0,    0,    0,    0,  202,  242,    0,    0,    0,    0,
  299,   21,   21,  243,   21,   21,   21,   21,   21,   21,
   21,   21,   21,  304,    2,  290,  -29, -109,    9,  312,
    0,    0,  233,    0,  331,    0,   -5,    0,    0,    0,
    0,    0,    0,    0, -136,  253,    0,   -2,  359,    0,
  -10, -101,    0,    0,  -41,    0,   51,    8,    8,    0,
    0,   89,    0,  160,  342,    0,    0,    0,  364,    0,
  -48,  267,    0,    0,    0,    0,    0,    0,    0,  123,
  123, -182,   71,  275,   22,   22,   22,   22,  113,  113,
    0,    0,    0,  132,  375,    2,  377,    0,  278,    0,
  388,    0,  150,    0,    0,    0,    0,    0,    0,  420,
    0,    0,    0,    0,    0,    0,    0,    0,   -9,   -9,
    0,    0,    0,    0,    0,  366,    0,  242,  410,  242,
  419,  442,  242,  448,    0,  242,  451,    0,  460,   94,
    0,    0,    0,    0,    0,    0,    0,    0,  461,  449,
    0,    0,  250,  252,    0,  254,  256,    0,    0,  279,
  282,    0, -139,  283,    0,  242,   -9,   -9,   -9,   -9,
  483,  487,  497,  512,  515,  516,  521,  524,  525,    0,
    0,  527,  511,    0,  535,  540,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   -9,   -9,  242,  242,  542,
  543,    0,    0,    0,    0,  242,  264,    0,    0,  -22,
    0,    0,    0,
};
final static short yyrindex[] = {                         0,
  591,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  382,    0,    0,    0,    0,  592,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  395,    0,    0,
  463,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  338,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  553,    0,   93,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,   41,  428,  277,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  169,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    7,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  175,    0,  190,    0,    0,    0,    0,    0,  475,
    0,    0,    0,    0,    0,    0,    0,    0,    0,   86,
  121,    0,    0,    0,  434,  440,  452,  456,  302,  339,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,
};
final static short yygindex[] = {                         0,
    0,  581,    0,    0,    0,   65,  498,    0,  499,   10,
    0,    0,  -16,  663, -112,  464,  393,  -67,   -4,  -99,
    0,  -34,  433,  468,  -17,  528,    0,   55,    0,  517,
  -19,    0,    0,    1, -154,  472, -126,    0,    0,    0,
    0,    0,    0,    0,    0,   47,  496,  345,   16,  413,
    0,    0,    0,    0,    0,  612,  486,    0,  458,    0,
    0,  526,  623,  672,  674,  774,  782,    0,    0,    0,
  624,  367, -246, -260,
};
final static int YYTABLESIZE=863;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                        288,
   28,   51,   61,  112,  127,   51,  100,   79,  111,  184,
  152,  214,  197,   28,   28,  270,  201,   28,  104,  112,
  148,  383,  220,  286,  111,  112,  180,   14,  124,   14,
  111,   77,  145,  283,  112,  111,  167,  120,  281,  111,
  265,  152,  268,  320,  100,  199,   43,   93,  112,  272,
  232,  221,  221,  111,  112,  234,   53,  128,  121,  111,
  112,   56,   94,   51,  189,  111,  190,   28,  169,  169,
   28,   51,  210,  211,  212,  213,   54,   66,   66,  356,
  302,   77,  162,  162,  147,  128,   56,  228,   14,   99,
  352,  353,   15,  151,  289,  287,   49,   56,  287,  164,
  164,  103,  229,  164,  164,  370,  371,  181,   55,  290,
   13,  312,   13,   14,  305,  144,  347,   15,  177,  178,
  179,   58,  221,  348,  151,  221,   78,   99,  198,   43,
   62,  134,  293,   22,  134,  169,   22,  337,   14,  277,
  169,  162,   15,   73,  164,  164,  269,  137,  162,  193,
  237,   22,  338,   90,  191,  237,   49,   50,  164,  192,
   80,   79,    9,   10,   11,  164,   56,  231,   14,   69,
  164,   13,   15,    8,  231,  164,  111,   92,    8,    9,
   10,   11,  187,   56,  188,   91,    8,    9,   10,   11,
  316,   56,  189,   96,  190,   14,   13,   49,   56,   15,
   95,   14,   97,  284,  119,   15,  261,  262,  263,   85,
  196,  122,   85,  298,  299,   19,  131,   22,  137,  125,
  135,   13,  174,   65,  146,   98,  267,   85,  250,  251,
   56,  200,  126,    8,  154,  125,  233,  219,  182,  183,
  123,  125,  238,  107,  108,  109,  110,  217,  126,  150,
  125,   13,   51,  280,  126,  176,   49,   56,   59,  107,
  108,  109,  110,  126,  271,  107,  108,  109,  110,  195,
  106,  108,  109,  110,  107,  108,  109,  110,   13,  194,
  133,   60,  291,  292,   13,   15,   15,  155,  107,  108,
  109,  110,  203,   85,  107,  108,  109,  110,  202,   19,
  107,  108,  109,  110,    1,    2,    3,   15,  204,    4,
   77,   77,    5,  205,   56,    6,    7,   92,  308,   92,
   92,   92,  207,    8,    9,   10,   11,    8,   12,   71,
    2,    3,  303,  304,    4,   92,   92,    5,   92,  249,
    6,    7,   93,  264,   93,   93,   93,  266,    8,    9,
   10,   11,  274,   12,   63,   78,   78,  275,   64,    4,
   93,   93,    5,   93,  239,  239,    7,   22,   22,   22,
   22,  276,   22,    8,    9,   10,   11,  279,   12,   94,
   51,   94,   94,   94,   63,  294,  239,  309,   68,    4,
   79,   79,    5,   88,  185,  186,    7,   94,   94,  282,
   94,   92,  314,    8,    9,   10,   11,   15,   12,   15,
  230,   63,  108,  109,  110,  132,    4,   63,  311,    5,
  313,  136,    4,    7,   85,    5,   93,  125,  315,    7,
    8,    9,   10,   11,  236,   12,    8,    9,   10,   11,
   75,   12,  125,   85,   85,   85,   85,  143,   85,   19,
   19,   19,   19,  325,   19,   72,    8,    9,   10,   11,
  317,   56,  328,   94,   56,   56,   56,   56,   80,   56,
    8,    9,   10,   11,   83,   56,    8,    9,   10,   11,
   84,   56,    9,   10,   11,  329,   56,   80,  295,   80,
  321,  332,   81,   83,  335,   83,   82,   63,  252,   84,
  336,   84,    4,    4,  339,    5,    5,  340,   76,    7,
    7,   81,  341,   81,  342,   82,  343,   82,  344,  380,
   75,   12,   12,   73,    4,   40,  357,    5,  300,  301,
  358,    7,   92,  259,  260,   72,  306,  307,   40,   40,
  359,  345,   40,   12,  346,  349,   92,   92,   92,   92,
  115,   92,   92,   92,   92,  360,   92,   93,  361,  362,
    8,    9,   10,   11,  363,   56,  226,  364,  365,  367,
  366,   93,   93,   93,   93,  368,   93,   93,   93,   93,
  369,   93,  376,  377,    8,    9,   10,   11,   82,   56,
    2,    1,   40,  125,   94,   40,  154,   72,  165,  166,
  206,  235,   83,   84,   85,   86,   87,  278,   94,   94,
   94,   94,  218,   94,   94,   94,   94,  149,   94,   63,
  175,   63,   41,  225,    4,   70,    4,    5,   57,    5,
  310,    7,    0,    7,    0,   41,   41,  125,    0,   41,
    0,  254,  138,   12,    0,   12,    0,    0,    0,    0,
   72,  125,  125,  125,  125,  125,  139,  140,  141,  142,
    0,  125,    0,    0,   72,   72,   72,   72,   72,  253,
    0,   42,    0,   43,  123,    0,  226,  115,  115,    0,
  255,  256,  257,  258,   42,   42,   43,   43,   42,   41,
   43,    0,   41,    0,  273,    0,  297,   80,   80,   80,
   80,  318,  318,   83,   83,   83,   83,    0,    0,   84,
   84,   84,   84,    0,    0,    0,    0,    0,   73,    0,
    0,   81,   81,   81,   81,   82,   82,   82,   82,    0,
   72,    0,   73,   73,   73,   73,   73,    0,   42,    0,
   43,   42,    0,   43,   72,   72,   72,   72,   72,  318,
  318,  354,  354,  322,    0,  324,    0,  327,    0,    0,
  331,  163,  163,  334,    0,  168,  168,    0,    0,    0,
    0,    0,    0,   44,    0,    0,    0,    0,  354,  354,
    0,   45,    0,  323,    0,  326,   44,   44,  330,    0,
   44,  333,    0,  351,   45,   45,    0,    0,   45,    0,
    0,    0,    0,    0,    0,    0,  215,  215,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  163,  350,    0,    0,    0,  373,  375,  163,    0,    0,
    0,    0,  168,  379,  382,    0,    0,  168,    0,    0,
   44,    0,    0,   44,    0,    0,    0,    0,   45,    0,
    0,   45,    0,  372,  374,    0,    0,    0,    0,    0,
    0,  378,  381,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         41,
    0,    1,    0,   40,   41,    5,   40,   25,   45,   41,
   40,  125,   58,   13,   14,  125,   41,   17,   40,   40,
   41,   44,   41,  125,   45,   40,   41,   40,   44,   40,
   45,   44,   40,   44,   40,   45,  125,  256,   41,   45,
  195,   40,  197,  290,   40,   40,   40,   44,   40,   41,
  163,  151,  152,   45,   40,  168,  280,   62,   58,   45,
   40,  280,   59,   63,   43,   45,   45,   67,  103,  104,
   70,   71,  140,  141,  142,  143,  280,   13,   14,  340,
  263,   41,   99,  100,   89,   90,  280,   44,   40,  123,
  337,  338,   44,  123,   44,  222,  279,  280,  225,   99,
  100,  123,   59,  103,  104,  366,  367,  112,   40,   59,
  123,  266,  123,   40,   44,  123,  256,   44,  281,  282,
  283,  264,  222,  263,  123,  225,   41,  123,  123,  123,
   40,   67,   44,   41,   70,  170,   44,   44,   40,  207,
  175,  158,   44,  280,  144,  145,  256,   59,  165,   37,
  170,   59,   59,   40,   42,  175,  279,  280,  158,   47,
  280,   41,  276,  277,  278,  165,  280,  158,   40,   41,
  170,  123,   44,  275,  165,  175,   45,   44,  275,  276,
  277,  278,   60,  280,   62,   46,  275,  276,  277,  278,
   41,  280,   43,   58,   45,   40,  123,  279,  280,   44,
   44,   40,  256,  221,  265,   44,  191,  192,  193,   41,
  256,   44,   44,  262,  263,   41,   44,  125,   59,  256,
   44,  123,   41,  125,   61,  259,  256,   59,  182,  183,
   41,  256,  269,  275,   40,  256,   41,  256,  270,  271,
  256,  256,   41,  280,  281,  282,  283,   41,  269,  280,
  256,  123,  252,  256,  269,   41,  279,  280,  256,  280,
  281,  282,  283,  269,  256,  280,  281,  282,  283,   58,
  256,  281,  282,  283,  280,  281,  282,  283,  123,  266,
  125,  279,  228,  229,  123,   44,   44,  280,  280,  281,
  282,  283,   44,  125,  280,  281,  282,  283,  125,  125,
  280,  281,  282,  283,  256,  257,  258,   44,   41,  261,
  270,  271,  264,   44,  125,  267,  268,   41,   44,   43,
   44,   45,   61,  275,  276,  277,  278,  275,  280,  256,
  257,  258,  262,  263,  261,   59,   60,  264,   62,   41,
  267,  268,   41,   40,   43,   44,   45,   58,  275,  276,
  277,  278,   41,  280,  256,  270,  271,  125,  260,  261,
   59,   60,  264,   62,  123,  123,  268,  275,  276,  277,
  278,   41,  280,  275,  276,  277,  278,  125,  280,   41,
  380,   43,   44,   45,  256,   44,  123,  256,  260,  261,
  270,  271,  264,   61,  272,  273,  268,   59,   60,   41,
   62,  125,  125,  275,  276,  277,  278,   44,  280,   44,
  125,  256,  281,  282,  283,  260,  261,  256,   44,  264,
   44,  260,  261,  268,  256,  264,  125,   46,   41,  268,
  275,  276,  277,  278,  125,  280,  275,  276,  277,  278,
   46,  280,   61,  275,  276,  277,  278,   61,  280,  275,
  276,  277,  278,   44,  280,   61,  275,  276,  277,  278,
   41,  280,   44,  125,  275,  276,  277,  278,   41,  280,
  275,  276,  277,  278,   41,  280,  275,  276,  277,  278,
   41,  280,  276,  277,  278,   44,  280,   60,  125,   62,
  125,   44,   41,   60,   44,   62,   41,  256,  256,   60,
   41,   62,  261,  261,   44,  264,  264,   59,   46,  268,
  268,   60,  263,   62,  263,   60,  263,   62,  263,  256,
   46,  280,  280,   61,  261,    0,   44,  264,  262,  263,
   44,  268,  256,  189,  190,   61,  262,  263,   13,   14,
   44,  263,   17,  280,  263,  263,  270,  271,  272,  273,
   55,  275,  276,  277,  278,   44,  280,  256,   44,   44,
  275,  276,  277,  278,   44,  280,  154,   44,   44,   59,
   44,  270,  271,  272,  273,   41,  275,  276,  277,  278,
   41,  280,   41,   41,  275,  276,  277,  278,  256,  280,
    0,    0,   67,  256,  256,   70,   44,   17,  100,  102,
  137,  169,  270,  271,  272,  273,  274,  215,  270,  271,
  272,  273,  145,  275,  276,  277,  278,   90,  280,  256,
  104,  256,    0,  152,  261,   14,  261,  264,    5,  264,
  264,  268,   -1,  268,   -1,   13,   14,  256,   -1,   17,
   -1,  184,  256,  280,   -1,  280,   -1,   -1,   -1,   -1,
  256,  270,  271,  272,  273,  274,  270,  271,  272,  273,
   -1,  280,   -1,   -1,  270,  271,  272,  273,  274,  184,
   -1,    0,   -1,    0,  280,   -1,  264,  182,  183,   -1,
  185,  186,  187,  188,   13,   14,   13,   14,   17,   67,
   17,   -1,   70,   -1,  199,   -1,  239,  270,  271,  272,
  273,  289,  290,  270,  271,  272,  273,   -1,   -1,  270,
  271,  272,  273,   -1,   -1,   -1,   -1,   -1,  256,   -1,
   -1,  270,  271,  272,  273,  270,  271,  272,  273,   -1,
  256,   -1,  270,  271,  272,  273,  274,   -1,   67,   -1,
   67,   70,   -1,   70,  270,  271,  272,  273,  274,  337,
  338,  339,  340,  296,   -1,  298,   -1,  300,   -1,   -1,
  303,   99,  100,  306,   -1,  103,  104,   -1,   -1,   -1,
   -1,   -1,   -1,    0,   -1,   -1,   -1,   -1,  366,  367,
   -1,    0,   -1,  298,   -1,  300,   13,   14,  303,   -1,
   17,  306,   -1,  336,   13,   14,   -1,   -1,   17,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,  144,  145,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
  158,  336,   -1,   -1,   -1,  368,  369,  165,   -1,   -1,
   -1,   -1,  170,  376,  377,   -1,   -1,  175,   -1,   -1,
   67,   -1,   -1,   70,   -1,   -1,   -1,   -1,   67,   -1,
   -1,   70,   -1,  368,  369,   -1,   -1,   -1,   -1,   -1,
   -1,  376,  377,
};
}
final static short YYFINAL=16;
final static short YYMAXTOKEN=283;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,"'%'",null,null,"'('","')'","'*'","'+'",
"','","'-'","'.'","'/'",null,null,null,null,null,null,null,null,null,null,"':'",
"';'","'<'","'='","'>'",null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,"'{'",null,"'}'",null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,"CLASS","INTERFACE","IMPLEMENT",
"RETURN","IF","ELSE","END_IF","FOR","IN","RANGE","IMPL","PRINT","TOD",
"EQUAL_OPERATOR","NOT_EQUAL_OPERATOR","GREATER_THAN_OR_EQUAL_OPERATOR",
"LESS_THAN_OR_EQUAL_OPERATOR","MINUS_ASSIGN","VOID","LONG","UINT","DOUBLE",
"CADENA","ID","CTE_DOUBLE","CTE_UINT","CTE_LONG",
};
final static String yyrule[] = {
"$accept : program",
"program : type_declarations",
"program : error",
"type_declarations : type_declaration",
"type_declarations : type_declarations type_declaration",
"type_declaration : class_declaration",
"type_declaration : interface_declaration",
"type_declaration : implement_for_declaration",
"type_declaration : block_statement",
"class_declaration : CLASS ID class_body",
"class_declaration : CLASS ID interfaces class_body",
"class_body : '{' class_body_declarations '}'",
"class_body : '(' class_body_declarations ')'",
"class_body_declarations : class_body_declaration",
"class_body_declarations : class_body_declarations class_body_declaration",
"class_body_declaration : class_member_declaration",
"class_member_declaration : field_declaration",
"class_member_declaration : method_declaration",
"field_declaration : type variable_declarators ','",
"field_declaration : type variable_declarators",
"variable_declarators : variable_declarator",
"variable_declarators : variable_declarators ';' variable_declarator",
"variable_declarator : variable_declarator_id",
"variable_declarator : variable_declarator_id '=' variable_initializer",
"variable_declarator : variable_declarator_id error '=' variable_initializer",
"variable_declarator : variable_declarator_id EQUAL_OPERATOR variable_initializer",
"variable_declarator : variable_declarator_id NOT_EQUAL_OPERATOR variable_initializer",
"variable_declarator : variable_declarator_id LESS_THAN_OR_EQUAL_OPERATOR variable_initializer",
"variable_declarator : variable_declarator_id GREATER_THAN_OR_EQUAL_OPERATOR variable_initializer",
"variable_declarator_id : ID",
"variable_initializer : arithmetic_operation",
"method_declaration : method_header method_body",
"method_header : result_type method_declarator",
"result_type : VOID",
"method_declarator : ID '(' formal_parameter ')'",
"method_declarator : ID '{' formal_parameter '}'",
"method_declarator : ID '(' formal_parameter error ')'",
"method_declarator : ID '(' ')'",
"method_declarator : ID '{' '}'",
"method_body : block",
"method_body : ','",
"formal_parameter : type variable_declarator_id",
"real_parameter : arithmetic_operation",
"interfaces : IMPLEMENT interface_type_list",
"interface_type_list : type_name",
"interface_type_list : interface_type_list ';' type_name",
"interface_type_list : interface_type_list ',' type_name",
"interface_declaration : INTERFACE ID interface_body",
"interface_body : '{' interface_member_declaration '}'",
"interface_body : '(' interface_member_declaration ')'",
"interface_body : '{' '}'",
"interface_body : '(' ')'",
"interface_member_declaration : interface_method_declaration",
"interface_member_declaration : interface_member_declaration interface_method_declaration",
"interface_method_declaration : constant_declaration",
"interface_method_declaration : abstract_method_declaration",
"constant_declaration : type variable_declarators",
"abstract_method_declaration : result_type method_declarator ','",
"implement_for_declaration : IMPL FOR reference_type ':' implement_for_body",
"implement_for_declaration : IMPL FOR reference_type ':' error ','",
"implement_for_declaration : IMPL FOR error ':' implement_for_body ','",
"implement_for_declaration : IMPL FOR reference_type error ':' implement_for_body",
"implement_for_declaration : error ID ':' implement_for_body",
"implement_for_body : '{' implement_for_body_declarations '}'",
"implement_for_body : '(' implement_for_body_declarations ')'",
"implement_for_body_declarations : implement_for_body_declaration",
"implement_for_body_declarations : implement_for_body_declarations implement_for_body_declaration",
"implement_for_body_declaration : implement_for_method_declaration",
"implement_for_method_declaration : method_header implement_for_method_body",
"implement_for_method_body : block",
"implement_for_method_body : ','",
"assignment : left_hand_side assignment_operator arithmetic_operation",
"left_hand_side : reference_type",
"left_hand_side : field_acces",
"field_acces : primary '.' ID",
"primary : reference_type",
"primary : field_acces",
"equality_expression : relational_expression",
"equality_expression : equality_expression EQUAL_OPERATOR relational_expression",
"equality_expression : equality_expression NOT_EQUAL_OPERATOR relational_expression",
"relational_expression : additive_expression",
"relational_expression : relational_expression '<' additive_expression",
"relational_expression : relational_expression '>' additive_expression",
"relational_expression : relational_expression GREATER_THAN_OR_EQUAL_OPERATOR additive_expression",
"relational_expression : relational_expression LESS_THAN_OR_EQUAL_OPERATOR additive_expression",
"arithmetic_operation : additive_expression",
"arithmetic_operation : TOD '(' additive_expression ')'",
"arithmetic_operation : TOD '(' error ')'",
"arithmetic_operation : TOD '{' error '}'",
"arithmetic_operation : TOD '{' '}'",
"arithmetic_operation : TOD '(' ')'",
"arithmetic_operation : error",
"additive_expression : multiplicative_expression",
"additive_expression : additive_expression '+' multiplicative_expression",
"additive_expression : additive_expression '-' multiplicative_expression",
"multiplicative_expression : unary_expression",
"multiplicative_expression : multiplicative_expression '*' unary_expression",
"multiplicative_expression : multiplicative_expression '/' unary_expression",
"multiplicative_expression : multiplicative_expression '%' unary_expression",
"unary_expression : factor",
"unary_expression : '(' arithmetic_operation ')'",
"unary_expression : '(' ')'",
"unary_expression : ID",
"factor : CTE_DOUBLE",
"factor : CTE_UINT",
"factor : CTE_LONG",
"factor : '-' CTE_DOUBLE",
"factor : '-' CTE_LONG",
"factor : '-' CTE_UINT",
"assignment_operator : '='",
"assignment_operator : MINUS_ASSIGN",
"assignment_operator : error '='",
"assignment_operator : EQUAL_OPERATOR",
"assignment_operator : NOT_EQUAL_OPERATOR",
"assignment_operator : LESS_THAN_OR_EQUAL_OPERATOR",
"assignment_operator : GREATER_THAN_OR_EQUAL_OPERATOR",
"method_invocation : ID '(' real_parameter ')'",
"method_invocation : ID '(' ')'",
"method_invocation : ID '(' real_parameter error ')'",
"method_invocation : field_acces '(' real_parameter ')'",
"method_invocation : field_acces '(' ')'",
"method_invocation : field_acces '(' real_parameter error ')'",
"type : primitive_type",
"type : reference_type",
"primitive_type : numeric_type",
"reference_type : ID",
"numeric_type : integral_type",
"numeric_type : floating_type",
"integral_type : UINT",
"integral_type : LONG",
"floating_type : DOUBLE",
"type_name : ID",
"block : '{' block_statements RETURN ',' '}'",
"block : '{' block_statements '}'",
"block : '(' block_statements RETURN ',' ')'",
"block : '{' RETURN ',' '}'",
"block : '(' RETURN ',' ')'",
"block : '{' '}'",
"block : '(' ')'",
"executable_block : '{' executable_block_statements '}'",
"executable_block : '{' '}'",
"block_statements : block_statement",
"block_statements : block_statements block_statement",
"executable_block_statements : executable_statement",
"executable_block_statements : executable_block_statements executable_statement",
"block_statement : local_variable_declaration_statement",
"block_statement : statement",
"executable_statement : if_then_statement",
"executable_statement : if_then_else_statement",
"executable_statement : for_in_range_statement",
"executable_statement : print_statement",
"executable_statement : expression_statement",
"executable_statement : empty_statement",
"local_variable_declaration_statement : local_variable_declaration ','",
"local_variable_declaration : type variable_declarators",
"statement : statement_without_trailing_substatement",
"statement : if_then_statement",
"statement : if_then_else_statement",
"statement : for_in_range_statement",
"statement : method_declaration",
"statement : print_statement",
"statement_without_trailing_substatement : block",
"statement_without_trailing_substatement : empty_statement",
"statement_without_trailing_substatement : expression_statement",
"expression_statement : statement_expression ','",
"expression_statement : statement_expression ';'",
"statement_expression : assignment",
"statement_expression : method_invocation",
"empty_statement : ','",
"if_then_statement : IF '(' equality_expression ')' executable_block END_IF ','",
"if_then_statement : IF '(' equality_expression ')' executable_statement END_IF ','",
"if_then_statement : IF '(' equality_expression ')' executable_statement ','",
"if_then_statement : IF '(' equality_expression ')' executable_block ','",
"if_then_statement : IF '(' equality_expression ')' error END_IF ','",
"if_then_statement : IF '(' error ')' executable_block END_IF ','",
"if_then_statement : IF '(' error ')' executable_statement END_IF ','",
"if_then_else_statement : IF '(' equality_expression ')' executable_block ELSE executable_block END_IF ','",
"if_then_else_statement : IF '(' equality_expression ')' executable_block ELSE executable_statement END_IF ','",
"if_then_else_statement : IF '(' equality_expression ')' executable_statement ELSE executable_statement END_IF ','",
"if_then_else_statement : IF '(' equality_expression ')' executable_statement ELSE executable_block END_IF ','",
"if_then_else_statement : IF '(' equality_expression ')' executable_statement ELSE executable_block error ','",
"if_then_else_statement : IF '(' error ')' executable_block ELSE executable_block END_IF ','",
"if_then_else_statement : IF '(' error ')' executable_block ELSE executable_statement END_IF ','",
"if_then_else_statement : IF '(' error ')' executable_statement ELSE executable_statement END_IF ','",
"if_then_else_statement : IF '(' error ')' executable_statement ELSE executable_block END_IF ','",
"for_in_range_statement : FOR for_variable IN RANGE '(' for_init ';' for_end ';' for_update ')' executable_block",
"for_in_range_statement : FOR for_variable IN RANGE '(' for_init ';' for_end ';' for_update ')' executable_statement",
"for_in_range_statement : FOR for_variable IN RANGE '(' for_init ',' for_end ',' for_update ')' executable_block",
"for_in_range_statement : FOR for_variable IN RANGE '(' for_init ',' for_end ',' for_update ')' executable_statement",
"for_in_range_statement : FOR for_variable IN RANGE '(' for_init ';' for_end ';' for_update ')' error ','",
"for_in_range_statement : FOR for_variable IN RANGE '(' error ')' executable_block",
"for_in_range_statement : FOR for_variable IN RANGE '(' error ')' executable_statement",
"for_in_range_statement : error for_variable error '(' for_init ';' for_end ';' for_update ')' executable_block",
"for_in_range_statement : error for_variable error '(' for_init ';' for_end ';' for_update ')' executable_statement",
"for_in_range_statement : error for_variable error '(' for_init ',' for_end ',' for_update ')' executable_block",
"for_in_range_statement : error for_variable error '(' for_init ',' for_end ',' for_update ')' executable_statement",
"for_variable : reference_type",
"for_init : factor",
"for_update : factor",
"for_end : factor",
"print_statement : PRINT CADENA ','",
"print_statement : PRINT CADENA error",
"print_statement : PRINT error ','",
"print_statement : error CADENA ','",
"print_statement : PRINT '\\000'",
};

//#line 420 "grammer.y"

private static AnalizadorLexico aLexico;
private static int yylval_recognition = 0;
public static boolean error = false;

// This method is the one where BYACC/J expects to obtain its input tokens. 
// Wrap any file/string scanning code you have in this function. This method should return <0 if there is an error, and 0 when it encounters the end of input. See the examples to clarify what we mean.
int yylex() {
  Tupla<String, Short> t = aLexico.generateToken();
  String lexema = t.getFirst();
  Short token = t.getSecond();

  if (lexema != null){
    yylval = new ParserVal(lexema);
    yylval_recognition += 1;
  }
  
  return token;
}

// This method is expected by BYACC/J, and is used to provide error messages to be directed to the channels the user desires.
void yyerror(String msg) {
    System.out.println("Error en el parser: " + msg + " in " + val_peek(0).sval);
}

// ###############################################################
// metodos auxiliares a la gramatica
// ###############################################################

  private String negarDouble(String lexema) {

    String n_lexema = lexema;

    try {
      n_lexema = String.valueOf(-Double.parseDouble(lexema));
    } catch (Exception ex) {
    }

    addTablaSimbolos(lexema, n_lexema, "D");

    return n_lexema;
  }


  private void addTablaSimbolos(String lexema, String n_lexema, String tipo) {

    if (!TablaSimbolos.containsKey(n_lexema)) {
      if (tipo == "D") { // Perdon Luis por hacer un if por tipos
        TablaSimbolos.addDouble(n_lexema);
      } else {
        TablaSimbolos.addLong(n_lexema);
      }
      TablaSimbolos.addContador(n_lexema);
    } else {
      TablaSimbolos.increaseCounter(n_lexema);
    }

    TablaSimbolos.decreaseCounter(lexema);
  }

  private String chequearRangoLong(String lexema) {

    long RDN_MAX = (long) Math.pow(2, 31) - 1;
    long number = 0;

    try {
      number = Long.parseLong(lexema);
    } catch (Exception ex) {
    }

    if (number > RDN_MAX) {
      Logger.logWarning(aLexico.getProgramPosition(),
          "El LONG se excedio de rango, el mismo fue truncado al valor " + RDN_MAX + ".");
      String n_lexema = String.valueOf(RDN_MAX);
      addTablaSimbolos(lexema, n_lexema, "L");

      return n_lexema;
    }

    return lexema;
  }


private String negarLong(String lexema) {
  
    long number = 0;

    try {
        number = -Long.parseLong(lexema);
    } catch (Exception ex) {}

    String n_lexema = String.valueOf(number);

    addTablaSimbolos(lexema, n_lexema, "L");

    return n_lexema;
}

// ###############################################################
// metodos de lectura de los programadas
// ###############################################################

private static ArrayList<String> listFilesInDirectory(String path) {
  // Obtén el directorio actual
  File element = new File(System.getProperty("user.dir") + "/" + path);
  ArrayList<String> out = new ArrayList<>();

  // Verifica si es un directorio o archivo válido
  if (element.isDirectory() || element.isFile()) {
    // Lista de archivos y directorios en el directorio actual
    File[] filesAndDirs = element.listFiles();

    // Itera a través de los archivos y directorios
    int i = 0;
    for (File fileOrDir : filesAndDirs) {
      String name = fileOrDir.getName();
      System.out.println("[" + i + "]" + ": " + name);
      out.add(name);
      i++;
    }
  } else {
    System.err.println("No es un directorio válido.");
  }

  return out;
}


private static String generatePath() {
  ArrayList<String> directories = listFilesInDirectory("sample_programs");
  String path = "";

  if (!directories.isEmpty()) {
    Scanner scanner = new Scanner(System.in);
    int indice = -1;

    while (indice < 0) {
      System.out.print("Ingrese el numero de carpeta a acceder: ");
      String input = scanner.nextLine();
   
      try {
        indice = Integer.parseInt(input);
      } catch (Exception ex) {
        indice = -1;
      } 

      if (indice < directories.size() && indice >= 0) {
        path = directories.get(indice);
        directories = listFilesInDirectory("sample_programs" + "/" + path);
      } else {
        System.out.println("El indice no es correcto, ingrese nuevamente...");
        indice = -1;
      }

    }

    if (!directories.isEmpty()) {
      indice = -1;

      while (indice < 0) {

        System.out.print("Ingrese el numero de archivo binario a compilar: ");
        String input = scanner.nextLine();

        try {
          indice = Integer.parseInt(input);
        } catch (Exception ex) {
          indice = -1;
        } 

        if (indice < directories.size() && indice >= 0) {
          path += "/" + directories.get(Integer.parseInt(input));
        } else {
          System.out.println("El indice no es correcto, ingrese nuevamente...");
          indice = -1;
        }

      }
    }
    scanner.close();
  }
  return path;
}

public static void main (String [] args) throws IOException {
    System.out.println("Iniciando compilacion... ");

    String input = generatePath();

    aLexico = new AnalizadorLexico(input);

    if ( !aLexico.hasReadWell() ) {
        return;
    }


    Parser aSintactico = new Parser();
    aSintactico.run();
    //aSintactico.dump_stacks(yylval_recognition);

    System.out.println(Logger.dumpLog());
    System.out.println(aLexico.getProgram());
}


//#line 939 "Parser.java"
//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
void yylexdebug(int state,int ch)
{
String s=null;
  if (ch < 0) ch=0;
  if (ch <= YYMAXTOKEN) //check index bounds
     s = yyname[ch];    //now get it
  if (s==null)
    s = "illegal-symbol";
  debug("state "+state+", reading "+ch+" ("+s+")");
}





//The following are now global, to aid in error reporting
int yyn;       //next next thing to do
int yym;       //
int yystate;   //current parsing state from state table
String yys;    //current token string


//###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
int yyparse()
{
boolean doaction;
  init_stacks();
  yynerrs = 0;
  yyerrflag = 0;
  yychar = -1;          //impossible char forces a read
  yystate=0;            //initial state
  state_push(yystate);  //save it
  val_push(yylval);     //save empty value
  while (true) //until parsing is done, either correctly, or w/error
    {
    doaction=true;
    if (yydebug) debug("loop"); 
    //#### NEXT ACTION (from reduction table)
    for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])
      {
      if (yydebug) debug("yyn:"+yyn+"  state:"+yystate+"  yychar:"+yychar);
      if (yychar < 0)      //we want a char?
        {
        yychar = yylex();  //get next token
        if (yydebug) debug(" next yychar:"+yychar);
        //#### ERROR CHECK ####
        if (yychar < 0)    //it it didn't work/error
          {
          yychar = 0;      //change it to default string (no -1!)
          if (yydebug)
            yylexdebug(yystate,yychar);
          }
        }//yychar<0
      yyn = yysindex[yystate];  //get amount to shift by (shift index)
      if ((yyn != 0) && (yyn += yychar) >= 0 &&
          yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
        {
        if (yydebug)
          debug("state "+yystate+", shifting to state "+yytable[yyn]);
        //#### NEXT STATE ####
        yystate = yytable[yyn];//we are in a new state
        state_push(yystate);   //save it
        val_push(yylval);      //push our lval as the input for next rule
        yychar = -1;           //since we have 'eaten' a token, say we need another
        if (yyerrflag > 0)     //have we recovered an error?
           --yyerrflag;        //give ourselves credit
        doaction=false;        //but don't process yet
        break;   //quit the yyn=0 loop
        }

    yyn = yyrindex[yystate];  //reduce
    if ((yyn !=0 ) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
      {   //we reduced!
      if (yydebug) debug("reduce");
      yyn = yytable[yyn];
      doaction=true; //get ready to execute
      break;         //drop down to actions
      }
    else //ERROR RECOVERY
      {
      if (yyerrflag==0)
        {
        yyerror("syntax error");
        yynerrs++;
        }
      if (yyerrflag < 3) //low error count?
        {
        yyerrflag = 3;
        while (true)   //do until break
          {
          if (stateptr<0)   //check for under & overflow here
            {
            yyerror("stack underflow. aborting...");  //note lower case 's'
            return 1;
            }
          yyn = yysindex[state_peek(0)];
          if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)
            {
            if (yydebug)
              debug("state "+state_peek(0)+", error recovery shifting to state "+yytable[yyn]+" ");
            yystate = yytable[yyn];
            state_push(yystate);
            val_push(yylval);
            doaction=false;
            break;
            }
          else
            {
            if (yydebug)
              debug("error recovery discarding state "+state_peek(0)+" ");
            if (stateptr<0)   //check for under & overflow here
              {
              yyerror("Stack underflow. aborting...");  //capital 'S'
              return 1;
              }
            state_pop();
            val_pop();
            }
          }
        }
      else            //discard this token
        {
        if (yychar == 0)
          return 1; //yyabort
        if (yydebug)
          {
          yys = null;
          if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
          if (yys == null) yys = "illegal-symbol";
          debug("state "+yystate+", error recovery discards token "+yychar+" ("+yys+")");
          }
        yychar = -1;  //read another
        }
      }//end error recovery
    }//yyn=0 loop
    if (!doaction)   //any reason not to proceed?
      continue;      //skip action
    yym = yylen[yyn];          //get count of terminals on rhs
    if (yydebug)
      debug("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+")");
    if (yym>0)                 //if count of rhs not 'nil'
      yyval = val_peek(yym-1); //get current semantic value
    yyval = dup_yyval(yyval); //duplicate yyval if ParserVal is used as semantic value
    switch(yyn)
      {
//########## USER-SUPPLIED ACTIONS ##########
case 1:
//#line 32 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio el programa.");}
break;
case 2:
//#line 33 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "No se reconocio el programa.");}
break;
case 9:
//#line 51 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una CLASS.");}
break;
case 10:
//#line 52 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una CLASS que implementa una interface.");}
break;
case 12:
//#line 56 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La declaracion de una clase debe estar delimitado por llaves \"{...}\".");}
break;
case 18:
//#line 70 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una declaracion de atributo/s.");}
break;
case 19:
//#line 71 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La sentencia debe terminar con ','.");}
break;
case 24:
//#line 80 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Las declaraciones de variables se deben hacer con el caracter '='.");}
break;
case 25:
//#line 81 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Declaracion de variable no valida. El caracter == no se permite en una declaracion...");}
break;
case 26:
//#line 82 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Declaracion de variable no valida. El caracter !! no se permite en una declaracion...");}
break;
case 27:
//#line 83 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Declaracion de variable no valida. El caracter <= no se permite en una declaracion.");}
break;
case 28:
//#line 84 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Declaracion de variable no valida. El caracter >= no se permite en una declaracion.");}
break;
case 34:
//#line 102 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio un metodo.");}
break;
case 35:
//#line 103 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La declaracion de un metodo debe estar delimitado por parentesis \"(...)\".");}
break;
case 36:
//#line 104 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Solo se permite la declaracion de un unico parametro formal.");}
break;
case 37:
//#line 105 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio un metodo.");}
break;
case 38:
//#line 106 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La declaracion de un metodo debe estar delimitado por parentesis \"(...)\".");}
break;
case 46:
//#line 126 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Las interfaces deben estar separadas por ';'.");}
break;
case 47:
//#line 129 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una INTERFACE.");}
break;
case 49:
//#line 133 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "El cuerpo de la interface debe estar delimitado por llaves \"{...}\".");}
break;
case 51:
//#line 135 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "El cuerpo de la interface debe estar delimitado por llaves \"{...}\".");}
break;
case 58:
//#line 152 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio un IMPL FOR.");}
break;
case 59:
//#line 153 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario implementar el cuerpo del metodo.");}
break;
case 60:
//#line 154 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Se debe referenciar a una clase.");}
break;
case 61:
//#line 155 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Declaracion de IMPL FOR no valida, no es correcta la signatura.");}
break;
case 62:
//#line 156 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Declaracion de IMPL FOR no valida, no es correcta la signatura.");}
break;
case 64:
//#line 160 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "El cuerpo de la interface debe estar delimitado por llaves \"{...}\".");}
break;
case 70:
//#line 174 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario implementar el metodo de la clase.");}
break;
case 71:
//#line 182 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una asignacion.");}
break;
case 78:
//#line 197 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica.");}
break;
case 79:
//#line 198 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica.");}
break;
case 81:
//#line 202 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica.");}
break;
case 82:
//#line 203 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica.");}
break;
case 83:
//#line 204 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica.");}
break;
case 84:
//#line 205 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica.");}
break;
case 86:
//#line 209 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una conversion explicita.");}
break;
case 87:
//#line 210 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "No se puede convertir la expresion declarada.");}
break;
case 88:
//#line 211 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "El metodo TOD debe estar delimitado por parentesis \"(...)\".");}
break;
case 89:
//#line 212 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "El metodo TOD debe estar delimitado por parentesis \"(...)\".");}
break;
case 90:
//#line 213 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario pasar una expresion aritmetica.");}
break;
case 91:
//#line 214 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "No es una expresion aritmetica valida.");}
break;
case 92:
//#line 217 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion aritmetica.");}
break;
case 101:
//#line 230 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Termino vacio.");}
break;
case 105:
//#line 238 "grammer.y"
{yyval = new ParserVal(chequearRangoLong(val_peek(0).sval));}
break;
case 106:
//#line 239 "grammer.y"
{yyval = new ParserVal(negarDouble(val_peek(0).sval));}
break;
case 107:
//#line 240 "grammer.y"
{System.out.println(val_peek(0).sval); yyval = new ParserVal(negarLong(val_peek(0).sval));}
break;
case 108:
//#line 241 "grammer.y"
{Logger.logError(aLexico.getProgramPosition() ,"Los tipos UINT deben ser sin signo."); yyval = new ParserVal(val_peek(0).sval);}
break;
case 111:
//#line 248 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Las asignaciones se deben hacer con el caracter '=' o '-='.");}
break;
case 112:
//#line 249 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Las asignaciones se deben hacer con el caracter '=' o '-='.");}
break;
case 113:
//#line 250 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Las asignaciones se deben hacer con el caracter '=' o '-='.");}
break;
case 114:
//#line 251 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Las asignaciones se deben hacer con el caracter '=' o '-='.");}
break;
case 115:
//#line 252 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Las asignaciones se deben hacer con el caracter '=' o '-='.");}
break;
case 116:
//#line 255 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a un metodo, con pj de parametro.");}
break;
case 117:
//#line 256 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a un metodo, sin pj de parametro.");}
break;
case 118:
//#line 257 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Solo se permite el pasaje de un parametro real.");}
break;
case 119:
//#line 258 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a un metodo desde una clase, con pj de parametro.");}
break;
case 120:
//#line 259 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a un metodo desde una clase, sin pj de parametro.");}
break;
case 121:
//#line 260 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Solo se permite el pasaje de un parametro real.");}
break;
case 133:
//#line 300 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el retorno del bloque.");}
break;
case 134:
//#line 301 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Un bloque debe estar delimitado por llaves \"{...} y es necesario declarar el retorno del bloque.");}
break;
case 136:
//#line 303 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Un bloque debe estar delimitado por llaves \"{...}\".");}
break;
case 137:
//#line 304 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el retorno del bloque.");}
break;
case 138:
//#line 305 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Un bloque debe estar delimitado por llaves \"{...}\".");}
break;
case 153:
//#line 333 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una declaracion de variable local.");}
break;
case 165:
//#line 354 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Se esperaba una \',\' en el final de la sentencia.");}
break;
case 169:
//#line 365 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF.");}
break;
case 170:
//#line 366 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF.");}
break;
case 171:
//#line 367 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el final de la sentencia de control IF.");}
break;
case 172:
//#line 368 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el final de la sentencia de control IF.");}
break;
case 173:
//#line 369 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el cuerpo de la sentencia de control IF.");}
break;
case 174:
//#line 370 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");}
break;
case 175:
//#line 371 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");}
break;
case 176:
//#line 375 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF ELSE.");}
break;
case 177:
//#line 376 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF ELSE.");}
break;
case 178:
//#line 377 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF ELSE.");}
break;
case 179:
//#line 378 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF ELSE.");}
break;
case 180:
//#line 379 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el END_IF de la sentencia de control IF.");}
break;
case 181:
//#line 380 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");}
break;
case 182:
//#line 381 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");}
break;
case 183:
//#line 382 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");}
break;
case 184:
//#line 383 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");}
break;
case 185:
//#line 387 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia FOR IN RANGE.");}
break;
case 186:
//#line 388 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia FOR IN RANGE.");}
break;
case 187:
//#line 389 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Las constantes de actualizacion deben estar separadas por ';'.");}
break;
case 188:
//#line 390 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Las constantes de actualizacion deben estar separadas por ';'.");}
break;
case 189:
//#line 391 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Cuerpo del FOR IN RANGE no valido.");}
break;
case 190:
//#line 392 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Condicion del FOR IN RANGE no valido.");}
break;
case 191:
//#line 393 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Condicion del FOR IN RANGE no valido.");}
break;
case 192:
//#line 394 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La signatura del FOR IN RANGE no es valida.");}
break;
case 193:
//#line 395 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La signatura del FOR IN RANGE no es valida.");}
break;
case 194:
//#line 396 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La signatura del FOR IN RANGE no es valida.");}
break;
case 195:
//#line 397 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "La signatura del FOR IN RANGE no es valida.");}
break;
case 200:
//#line 413 "grammer.y"
{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia PRINT.");}
break;
case 201:
//#line 414 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Se esperaba una \',\' en el final de la sentencia.");}
break;
case 202:
//#line 415 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Se esperaba una cadena.");}
break;
case 203:
//#line 416 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Declaracion de PRINT no valida.");}
break;
case 204:
//#line 417 "grammer.y"
{Logger.logError(aLexico.getProgramPosition(), "Se esperaba un % que cierre la cadena.");}
break;
//#line 1476 "Parser.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
    }//switch
    //#### Now let's reduce... ####
    if (yydebug) debug("reduce");
    state_drop(yym);             //we just reduced yylen states
    yystate = state_peek(0);     //get new state
    val_drop(yym);               //corresponding value drop
    yym = yylhs[yyn];            //select next TERMINAL(on lhs)
    if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL
      {
      if (yydebug) debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
      yystate = YYFINAL;         //explicitly say we're done
      state_push(YYFINAL);       //and save it
      val_push(yyval);           //also save the semantic value of parsing
      if (yychar < 0)            //we want another character?
        {
        yychar = yylex();        //get next character
        if (yychar<0) yychar=0;  //clean, if necessary
        if (yydebug)
          yylexdebug(yystate,yychar);
        }
      if (yychar == 0)          //Good exit (if lex returns 0 ;-)
         break;                 //quit the loop--all DONE
      }//if yystate
    else                        //else not done yet
      {                         //get next state and push, for next yydefred[]
      yyn = yygindex[yym];      //find out where to go
      if ((yyn != 0) && (yyn += yystate) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
        yystate = yytable[yyn]; //get new state
      else
        yystate = yydgoto[yym]; //else go to new defred
      if (yydebug) debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
      state_push(yystate);     //going again, so push state & val...
      val_push(yyval);         //for next action
      }
    }//main loop
  return 0;//yyaccept!!
}
//## end of method parse() ######################################



//## run() --- for Thread #######################################
/**
 * A default run method, used for operating this parser
 * object in the background.  It is intended for extending Thread
 * or implementing Runnable.  Turn off with -Jnorun .
 */
public void run()
{
  yyparse();
}
//## end of method run() ########################################



//## Constructors ###############################################
/**
 * Default constructor.  Turn off with -Jnoconstruct .

 */
public Parser()
{
  //nothing to do
}


/**
 * Create a parser, setting the debug to true or false.
 * @param debugMe true for debugging, false for no debug.
 */
public Parser(boolean debugMe)
{
  yydebug=debugMe;
}
//###############################################################



}
//################### END OF CLASS ##############################
