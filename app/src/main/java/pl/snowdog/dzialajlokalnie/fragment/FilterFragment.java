package pl.snowdog.dzialajlokalnie.fragment;


import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.Spinner;

import com.activeandroid.query.Select;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemSelect;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import de.greenrobot.event.EventBus;
import pl.snowdog.dzialajlokalnie.DlApplication;
import pl.snowdog.dzialajlokalnie.R;
import pl.snowdog.dzialajlokalnie.adapter.CategoryAdapter;
import pl.snowdog.dzialajlokalnie.adapter.DistrictAdapter;
import pl.snowdog.dzialajlokalnie.events.FilterChangedEvent;
import pl.snowdog.dzialajlokalnie.model.Category;
import pl.snowdog.dzialajlokalnie.model.District;

/**
 * Created by bartek on 09.07.15.
 */
//TODO support DialogFragment DialogStyle się wywala w AndroidAnnotations dlaczego?
    // issue jest znane: https://github.com/excilys/androidannotations/issues/1435
@EFragment(R.layout.fragment_filter)
public class FilterFragment extends DialogFragment {

    @ViewById(R.id.spDistrict)
    Spinner spinner;

    @ViewById(R.id.recyclerView)
    RecyclerView recyclerView;

    @ViewById(R.id.btSet)
    Button setButton;

    @ViewById(R.id.btCancel)
    Button cancelButton;

    private DistrictAdapter adapter;
    private CategoryAdapter categoriesAdapter;


    @AfterViews
    void afterViews() {
        getDialog().setTitle(R.string.action_filter);

        List<District> districts = new Select().from(District.class).orderBy("name").execute();
        districts.add(0, new District(-1, getString(R.string.all_districts_filter), null, 16.934167, 52.408333, -1));
        adapter = DistrictAdapter.build(getActivity(), districts);
        if (DlApplication.filter.getDistrict() != null) {
            adapter.setSelectionId(DlApplication.filter.getDistrict().getDistrictID());
        }
        spinner.setAdapter(adapter);
        spinner.setSelection(adapter.getSelection());

        List<Category> categories = new Select().from(Category.class).orderBy("name").execute();
        categoriesAdapter = new CategoryAdapter(categories);
        categoriesAdapter.setSelectedCategories(DlApplication.filter.getCategories());
        recyclerView.setAdapter(categoriesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Click(R.id.btSet)
    void setClick() {
        if (adapter.getSelectedItem().getDistrictID() != -1) {
            DlApplication.filter.setDistrict(adapter.getSelectedItem());
        } else {
            DlApplication.filter.setDistrict(null);
        }
        DlApplication.filter.setCategories(categoriesAdapter.getSelectedCategories());

        EventBus.getDefault().post(new FilterChangedEvent());
        dismiss();
    }

    @Click(R.id.btCancel)
    void cancelClick() {
        dismiss();
    }

    @ItemSelect(R.id.spDistrict)
    void districtSelected(boolean selected, int position) {
        if (selected) {
            adapter.setSelection(position);
        }
    }
}