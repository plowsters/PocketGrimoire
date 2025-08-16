package com.example.pocketgrimoire;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.pocketgrimoire.database.PocketGrimoireRepository;
import com.example.pocketgrimoire.database.entities.Abilities;
import com.example.pocketgrimoire.viewmodel.RandomAbilityViewModel;

public class RandomAbilityActivity extends AppCompatActivity {

    private RandomAbilityViewModel vm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_ability);

        TextView title = findViewById(R.id.textTitle);
        TextView subtitle = findViewById(R.id.textSubtitle);
        Button btnNext = findViewById(R.id.btnNext);
        Button btnShare = findViewById(R.id.btnShare);

        final PocketGrimoireRepository repository = new PocketGrimoireRepository(getApplication());

        vm = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            @SuppressWarnings("unchecked")
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new RandomAbilityViewModel(repository);
            }
        }).get(RandomAbilityViewModel.class);

        vm.ability().observe(this, a -> {
            if (a == null) return;
            title.setText(a.getName());
            subtitle.setText(a.isTraitOrFeat() ? "Trait/Feat" : "Ability");
        });

        vm.error().observe(this, e -> {
            if (e != null && !e.isEmpty()) {
                Toast.makeText(this, "Error: " + e, Toast.LENGTH_SHORT).show();
            }
        });

        btnNext.setOnClickListener(v -> vm.next());
        btnShare.setOnClickListener(v -> {
            Abilities a = vm.ability().getValue();
            if (a == null) return;
            String text = a.getName() + (a.isTraitOrFeat() ? " (Trait/Feat)" : "");
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(i, "Share ability"));
        });

        vm.next();
    }
}