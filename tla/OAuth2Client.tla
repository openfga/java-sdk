--------------------------- MODULE OAuth2Client ---------------------------
EXTENDS Naturals, FiniteSets, TLC

CONSTANTS Threads, BUGGY

ASSUME Cardinality(Threads) >= 2

NONE == 0

(* --algorithm OAuth2Client {
  variables
    valid      = FALSE,
    inFlight   = NONE,
    monitor    = NONE,
    exchanges  = 0,
    tokens     = [t \in Threads |-> 0];

  define {
    AtMostOneExchange == exchanges <= 1
    AllDone           == \A t \in Threads : tokens[t] = 1
  }

  process (Thr \in Threads)
  variables joined = NONE;
  {
    HotPath:
      if (valid) {
        tokens[self] := 1;
        goto Finish;
      };

    Acquire:
      await monitor = NONE;
      monitor := self;

    UnderMonitor:
      if (~BUGGY /\ valid) {
        monitor := NONE;
        tokens[self] := 1;
        goto Finish;
      } else if (inFlight # NONE) {
        joined := inFlight;
        monitor := NONE;
        goto AwaitJoined;
      } else {
        inFlight := self;
        exchanges := exchanges + 1;
        monitor := NONE;
        goto DoExchange;
      };

    DoExchange:
      valid := TRUE;
      inFlight := NONE;
      tokens[self] := 1;
      goto Finish;

    AwaitJoined:
      await valid;
      tokens[self] := 1;
      goto Finish;

    Finish: skip;
  }
}
*)
\* BEGIN TRANSLATION
VARIABLES valid, inFlight, monitor, exchanges, tokens, pc

(* define statement *)
AtMostOneExchange == exchanges <= 1
AllDone           == \A t \in Threads : tokens[t] = 1

VARIABLE joined

vars == << valid, inFlight, monitor, exchanges, tokens, pc, joined >>

ProcSet == (Threads)

Init == (* Global variables *)
        /\ valid = FALSE
        /\ inFlight = NONE
        /\ monitor = NONE
        /\ exchanges = 0
        /\ tokens = [t \in Threads |-> 0]
        (* Process Thr *)
        /\ joined = [self \in Threads |-> NONE]
        /\ pc = [self \in ProcSet |-> "HotPath"]

HotPath(self) == /\ pc[self] = "HotPath"
                 /\ IF valid
                       THEN /\ tokens' = [tokens EXCEPT ![self] = 1]
                            /\ pc' = [pc EXCEPT ![self] = "Finish"]
                       ELSE /\ pc' = [pc EXCEPT ![self] = "Acquire"]
                            /\ UNCHANGED tokens
                 /\ UNCHANGED << valid, inFlight, monitor, exchanges, joined >>

Acquire(self) == /\ pc[self] = "Acquire"
                 /\ monitor = NONE
                 /\ monitor' = self
                 /\ pc' = [pc EXCEPT ![self] = "UnderMonitor"]
                 /\ UNCHANGED << valid, inFlight, exchanges, tokens, joined >>

UnderMonitor(self) == /\ pc[self] = "UnderMonitor"
                      /\ IF ~BUGGY /\ valid
                            THEN /\ monitor' = NONE
                                 /\ tokens' = [tokens EXCEPT ![self] = 1]
                                 /\ pc' = [pc EXCEPT ![self] = "Finish"]
                                 /\ UNCHANGED << inFlight, exchanges, joined >>
                            ELSE /\ IF inFlight # NONE
                                       THEN /\ joined' = [joined EXCEPT ![self] = inFlight]
                                            /\ monitor' = NONE
                                            /\ pc' = [pc EXCEPT ![self] = "AwaitJoined"]
                                            /\ UNCHANGED << inFlight, 
                                                            exchanges >>
                                       ELSE /\ inFlight' = self
                                            /\ exchanges' = exchanges + 1
                                            /\ monitor' = NONE
                                            /\ pc' = [pc EXCEPT ![self] = "DoExchange"]
                                            /\ UNCHANGED joined
                                 /\ UNCHANGED tokens
                      /\ valid' = valid

DoExchange(self) == /\ pc[self] = "DoExchange"
                    /\ valid' = TRUE
                    /\ inFlight' = NONE
                    /\ tokens' = [tokens EXCEPT ![self] = 1]
                    /\ pc' = [pc EXCEPT ![self] = "Finish"]
                    /\ UNCHANGED << monitor, exchanges, joined >>

AwaitJoined(self) == /\ pc[self] = "AwaitJoined"
                     /\ valid
                     /\ tokens' = [tokens EXCEPT ![self] = 1]
                     /\ pc' = [pc EXCEPT ![self] = "Finish"]
                     /\ UNCHANGED << valid, inFlight, monitor, exchanges, 
                                     joined >>

Finish(self) == /\ pc[self] = "Finish"
                /\ TRUE
                /\ pc' = [pc EXCEPT ![self] = "Done"]
                /\ UNCHANGED << valid, inFlight, monitor, exchanges, tokens, 
                                joined >>

Thr(self) == HotPath(self) \/ Acquire(self) \/ UnderMonitor(self)
                \/ DoExchange(self) \/ AwaitJoined(self) \/ Finish(self)

(* Allow infinite stuttering to prevent deadlock on termination. *)
Terminating == /\ \A self \in ProcSet: pc[self] = "Done"
               /\ UNCHANGED vars

Next == (\E self \in Threads: Thr(self))
           \/ Terminating

Spec == Init /\ [][Next]_vars

Termination == <>(\A self \in ProcSet: pc[self] = "Done")

\* END TRANSLATION
=============================================================================

