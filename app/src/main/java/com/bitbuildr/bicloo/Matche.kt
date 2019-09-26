package com.bitbuildr.bicloo

data class Matche(
    val id: Int,
    val utcDate: String,
    val winner: String?,
    val scoreHomeTeam: Int,
    val scoreAwayTeam: Int,
    val homeTeam: String,
    val awayTeam: String
)

/*
"id": 266391,
"season": {
    "id": 495,
    "startDate": "2019-06-25",
    "endDate": "2020-05-30",
    "currentMatchday": 2
},
"utcDate": "2019-06-25T11:00:00Z",------------
"status": "FINISHED",
"matchday": null,
"stage": "PRELIMINARY_SEMI_FINALS",
"group": "Preliminary Semi-finals",
"lastUpdated": "2019-09-26T06:30:06Z",
"score": {------------
    "winner": "AWAY_TEAM",------------
    "duration": "REGULAR",
    "fullTime": {------------
        "homeTeam": 0,
        "awayTeam": 1
    },
    "halfTime": {------------
        "homeTeam": 0,
        "awayTeam": 0
    },
    "extraTime": {
        "homeTeam": null,
        "awayTeam": null
    },
    "penalties": {
        "homeTeam": null,
        "awayTeam": null
    }
},
"homeTeam": {
    "id": 8102,
    "name": "SP Tre Penne"------------
},
"awayTeam": {
    "id": 1879,
    "name": "FC Santa Coloma"------------
},
"referees": [
{
    "id": 38757,
    "name": "Ian McNabb",
    "nationality": null
},
{
    "id": 38600,
    "name": "Paul Robinson",
    "nationality": null
},
{
    "id": 38758,
    "name": "Stephen Bell",
    "nationality": null
},
{
    "id": 38477,
    "name": "Jamie Robinson",
    "nationality": null
}
]
},*/