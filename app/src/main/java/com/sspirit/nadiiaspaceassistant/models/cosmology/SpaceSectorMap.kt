package com.sspirit.nadiiaspaceassistant.models.cosmology

import com.sspirit.nadiiaspaceassistant.utils.flatArrayMap

class SpaceSectorMap(
    val systems: Array<SpaceSystem>
) {
    val objects: List<SpaceObject>
        get() = systems.flatMap { it.objects }

    val pois: List<SpacePOI>
        get() = objects.flatMap { it.pois }

    val places: List<SpacePOIPlace>
        get() = pois.flatArrayMap { it.places }

    companion object {
        val empty = SpaceSectorMap(arrayOf())
    }

    fun systemBy(id: String): SpaceSystem? = systems.firstOrNull { it.id == id }
    fun objectBy(id: String): SpaceObject? = objects.firstOrNull { it.id == id }
    fun poiBy(id: String): SpacePOI? = pois.firstOrNull { it.id == id }
    fun placeBy(id: String): SpacePOIPlace? = places.firstOrNull { it.id == id }

    fun indicesOf(system: SpaceSystem): Array<Int> {
        return arrayOf(systems.indexOf(system))
    }

    fun indicesOf(obj: SpaceObject): Array<Int> {
        val index = obj.parent.objects.indexOf(obj)
        return indicesOf(obj.parent).plus(index)
    }

    fun indicesOf(poi: SpacePOI): Array<Int> {
        val index = poi.parent.pois.indexOf(poi)
        return indicesOf(poi.parent).plus(index)
    }

    fun indicesOf(place: SpacePOIPlace): Array<Int> {
        val index = place.parent.places.indexOf(place)
        return indicesOf(place.parent).plus(index)
    }

    fun filteredPOI(filter: (SpacePOI) -> Boolean): Array<SpacePOI> =
        pois.filter(filter).toTypedArray()
}