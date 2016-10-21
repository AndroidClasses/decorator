package com.eyeem.decorator.sample;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.eyeem.decorator.sample.adapter.DummyAdapter;
import com.eyeem.decorator.sample.decorators.EmptyInstigator;
import com.eyeem.recyclerviewtools.adapter.WrapAdapter;

public class MainActivity extends DecoratedAct {

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      preEnsureBindInstigator(EmptyInstigator.class);
      super.onCreate(savedInstanceState);

      setContentView(getLayoutId());
      onViewInflated();

      initRecyclerView();

      onViewCreated();
   }

   /**
    * bind the existing Instigator or create a new instance with default class if not existing then
    * bind it before instancing activity.
    *
    * @param klass, the default Instigator decorator class, which will instance specific adapter in
    *               the future, e.g., EmptyAdapter by EmptyInstigator, and delegate the recycler
    *               view setup, and respond to click action to recycler view item.
    **/
   private void preEnsureBindInstigator(Class<? extends Deco> klass) {
      if (getIntent() == null ||
              getIntent().getExtras() == null ||
              !getIntent().getExtras().containsKey(KEY.BUILDER)) {
         getIntent().putExtra(KEY.BUILDER, new Builder().addDecorator(klass));
      }

      bind(getBuilder(getIntent().getSerializableExtra(KEY.BUILDER)));
   }

   private void initRecyclerView() {
      RecyclerView rv = (RecyclerView) findViewById(R.id.recycler);
      rv.setLayoutManager(getLayoutManager());

      RecyclerView.Adapter adapter = getAdapter();
      if (adapter == null) {
         adapter = new DummyAdapter(this, getDecorators().getList());
      }

      WrapAdapter wrapAdapter;
      if (adapter instanceof WrapAdapter) {
         wrapAdapter = (WrapAdapter) adapter;
         adapter = wrapAdapter.getWrapped();
      } else {
         wrapAdapter = new WrapAdapter(adapter);
      }

      rv.setAdapter(wrapAdapter);
      setupRecyclerView(rv, wrapAdapter, adapter);
   }

   @Override protected void onDestroy() {
      super.onDestroy();
      unbind();
   }
}