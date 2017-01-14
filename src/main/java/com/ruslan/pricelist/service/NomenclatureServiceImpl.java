package com.ruslan.pricelist.service;

import com.ruslan.pricelist.beans.Distributor;
import com.ruslan.pricelist.beans.Item;
import com.ruslan.pricelist.beans.Nomenclature;
import com.ruslan.pricelist.utility.NomenclatureParser;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * Created by Ruslan on 12/28/2016.
 */
@Service("nomeclatureService")
public class NomenclatureServiceImpl implements NomenclatureService {

    private List<Nomenclature> nomenclatures ;

    @Override
    public List<Nomenclature> getNomenclatures() throws IOException {
        nomenclatures = NomenclatureParser.getNomenclaturesFromFile();
        return nomenclatures;
    }

    @Override
    public void removeNomenclature(Nomenclature nomenclature) {
        try {
            NomenclatureParser.removeNomenclature(nomenclature.getName());
            nomenclatures.remove(nomenclature);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addNomenclature(Nomenclature nomenclature) {
        if(nomenclatures.contains(nomenclature))
            throw new IllegalArgumentException("Такая номенклатура уже существует в списке!");
        try {
            NomenclatureParser.addNewNomenclature(nomenclature.getName());
            nomenclatures.add(nomenclature);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void removeAllNomenclatures() {
        try {
            NomenclatureParser.clearNomenclatures();
            nomenclatures.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void generateNomenclaturesByPriceLists(List<Distributor> allDistributors) {
        try{
            if(nomenclatures != null )
                removeAllNomenclatures();
            getNomenclatures();
            Set<Nomenclature> hashSet = new LinkedHashSet<>();

            for (Distributor distributor : allDistributors) {
                hashSet.addAll(converter(distributor.getPriceList()));
            }
            hashSet.forEach(this::addNomenclature);
        }catch (IllegalArgumentException ex){
            // implement logging
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Nomenclature> converter(List<Item> items){
        List<Nomenclature> nomenclatureList = new ArrayList<>();
        Long id;
        if(nomenclatures.size() != 0)
           id = nomenclatures.get(nomenclatures.size()-1).getId();
        else id = 0L;
        items.stream().forEach(item -> {
            nomenclatureList.add(new Nomenclature(id +1,item.getName()));
        });
        return nomenclatureList;
    }

}
